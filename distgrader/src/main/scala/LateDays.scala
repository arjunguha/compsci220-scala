package grading


object LateDays {

  import edu.umass.cs.extras.Implicits._
  import edu.umass.cs.CSV
  import edu.umass.cs.mail.Message
  import edu.umass.cs.mail.Implicits._
  import java.time.format.DateTimeFormatter
  import java.time.temporal.TemporalAmount
  import java.time.{LocalDateTime, LocalDate}
  import java.nio.file.{Paths, Files, Path}
  import java.time.temporal.ChronoUnit

  val accomodations = Map(
    "Lily Zhu" -> 2,
    "Kevin Hall" -> 2,
    "Russell Phelan" -> 2,
    "Vlad Emelyanov" -> 2,
    "Em Chiu" -> 4)

  val assignmentRegex = """^HW(\d+).*$""".r

  case class LateSubmission(assignment: String, daysLate: Int, penalty: Int) {
    override def toString(): String = {
      s"- Submitted $assignment $daysLate day(s) late (penalty: $penalty)"
    }
  }

  case class Submission(assignment: String, submitterName: String, submitterEmail: String,
    lateness: Int) {

    val assignmentNumber = assignmentRegex.findFirstMatchIn(assignment).get.group(1).toInt

  }



  val lateRegex = new scala.util.matching.Regex(
    """(?:(\d+) day(?:s)?)?(?: (\d+) hour(?:s)?)?(?: \d+ min(?:s)?)?(?: \d+ sec(?:s)?)? late""",
    "days", "hours")

  object Submission {

    def parse(assignment: String, row: List[String]): Option[Submission] = {
      val status = row(3)
      if (status.startsWith("No submission")) {
        None
      }
      else if (status == "Submitted for grading - Graded") {
        None
      }
      else if (status.startsWith("Submitted for grading - Graded - Extension granted until:")) {
        None
      }
      else {
        val lateDays_ = lateRegex.findFirstMatchIn(status) match {
          case Some(regexMatch) => {
            (regexMatch.group("days"), regexMatch.group("hours")) match {
              case (null, null) => 0
              case (null, _) => 1
              case (days, "") => days.toInt
              case (days, _) => days.toInt + 1
            }
          }
          case None => throw new IllegalArgumentException(s"Could not parse $status")
        }
        val fullName = row(1)
        val emailAddress = row(2)
        val lateDays = math.max(0, lateDays_ - accomodations.getOrElse(fullName, 0))
        if (lateDays == 0) None
        else Some(Submission(assignment, fullName, emailAddress, lateDays))
      }
    }
  }

/*
  val moodleDateTime = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy, h:mm a")
  val myDtFormat = DateTimeFormatter.ofPattern("MMM d yyyy, h a")


  def getLatePeriod(row: List[String]): (String, Int) = {
    val cell = row(3)
    val late = if (cell.contains("Submitted for grading") == false) {
      0
    }
    else if (cell.contains("late")) {
     lateRegex.findFirstMatchIn(cell) match {
       case Some(regexMatch) => {
         (regexMatch.group("days"), regexMatch.group("hours")) match {
           case (null, null) => 0
           case (null, _) => 1
           case (days, "") => days.toInt
           case (days, _) => days.toInt + 1
         }
       }
       case None => throw new IllegalArgumentException(s"Could not parse $cell")
     }
    }
    else {
      0
    }
    val user = row(2)
    user -> late
  }

  // Maps each student to the number of days late
  def processFile(cumulative: Map[String, Int], filename: Path): Map[String, Int] = {
    println(s"Processing $filename")
    moodleGradingSheetRegex.findFirstMatchIn(filename.getFileName.toString) match {
      case None => throw new IllegalArgumentException(s"Cannot parse CSV filename: ${filename.getFileName.toString}")
      case Some(regexMatch) => {
        val assignment = regexMatch.group("assignment")
        val deadline = dueDates(assignment)
        val header :: data = CSV.fromFile(filename)
        val newData = data.map(row => {
          val (id, daysLate) = getLatePeriod(row)
          val lateSoFar = cumulative.getOrElse(id, 0)
          val totalLate = lateSoFar + daysLate
          if (daysLate == 0) {
            row
          }
          else if (totalLate <= 4) {
            val comments = row(9)
            val newComments = s"This assignment was $daysLate day(s) late. You've now used $totalLate late days."
            println(s"Grade for $id on $assignment not changed. Explanation: $newComments")
            row.updated(9, newComments ++ comments)
          }
          else {
            val penalty = (math.min(daysLate, totalLate - 4))
            val grade = row(4).toFloat
            val newGrade = math.max(0, grade - 10 * penalty)
            val comments = row(9)
            val newComments = s"This assignment was $daysLate day(s) late. You had already used $totalLate late days. " +
              s"Therefore, the grade has been reduced by 10 * $penalty percentage points."
            println(s"Grade for $id on $assignment changed to $newGrade from $grade. Explanation: $newComments")
            row.updated(4, newGrade.toString).updated(9, newComments ++ comments)
          }
        })
        CSV.save(filename, header :: newData)
        val newCumulative = cumulative.combine(data.map(getLatePeriod).toMap)(_ + _, identity, identity)
        newCumulative
      }
    }
  }

  implicit val LocalDateTimeOrdering = new math.Ordering[LocalDateTime] {
    def compare(x: LocalDateTime, y: LocalDateTime): Int = x.compareTo(y)
  }


  */

  def prepareEmail(email: String, assignments: List[LateSubmission]): Unit = {
    val body = """
      |This email is about late homework submitted for COMPSCI220. Recall that the late policy is online:
      |
      | https://people.cs.umass.edu/~arjun/courses/compsci220-fall2016/policies/#late
      |
      |According to Moodle, you have used up the four late days allowed in the following way:
      |""".stripMargin +
    assignments.mkString("\n") +
    """
      |If you believe this is an error, please let us by Friday.
      |""".stripMargin

      val msg = Message(from = "arjun@cs.umass.edu", to = email, subj = "COMPSCI220 - late assignments", msg = body)
      // println(msg.send())
      println(email)
      println(body)
  }

  def processSheet(sheet: MoodleSheet): Seq[Submission] = {
    sheet.rows.tail.map(row => Submission.parse(sheet.assignment.get, row)).flatten
  }

  def lateReport(submissions: List[Submission]): List[LateSubmission] = {

    def process(cumulative: Int, submissions: List[Submission]): List[LateSubmission] = submissions match {
      case Nil => Nil
      case sub :: rest => {
        val thisSub = if (cumulative > 4) {
          LateSubmission(sub.assignment, sub.lateness, sub.lateness * -10)
        }
        else if (cumulative + sub.lateness > 4) {
          // cum + late = x where x > 4
          // penalty = (cum + late) - 4
          val penalty = (cumulative + sub.lateness) - 4
          LateSubmission(sub.assignment, sub.lateness, penalty * -10)
        }
        else {
          LateSubmission(sub.assignment, sub.lateness, 0)
        }
        thisSub :: process(cumulative + sub.lateness, rest)
      }
    }
    process(0, submissions)
  }

  def fromDirectory(dir: String): Unit = {
    import scala.collection.JavaConversions._

    val stream = Files.newDirectoryStream(Paths.get(dir))
    val csvs = stream.filter(_.getFileName.toString.endsWith(".csv"))

    val lateSubmissions = csvs.map(MoodleSheet.apply).flatMap(processSheet)
      .groupBy(_.submitterEmail)
      .mapValues(_.toList.sortBy(_.assignmentNumber))
      .filter { case (_, submissions) => submissions.map(_.lateness).sum > 4 }
      .map({ case (name, subs) => (name, lateReport(subs)) })

    for ((email, assignments) <- lateSubmissions) {
      prepareEmail(email, assignments)
    }

//    val csvs = stream.filter(p => p.getFileName.toString.endsWith(".csv")).toList.sortBy(filenameToDueDate)
//    stream.close()
//    val latePeriods = csvs.foldLeft(Map[String, Int]())(processFile)
  }

}

