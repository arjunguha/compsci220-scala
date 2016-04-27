package grading


object LateDays {

  import edu.umass.cs.extras.Implicits._
  import edu.umass.cs.CSV
  import edu.umass.cs.mail.sendEmail
  import edu.umass.cs.mail.Implicits._
  import java.time.format.DateTimeFormatter
  import java.time.{LocalDateTime, LocalDate}
  import java.nio.file.{Paths, Files, Path}
  import java.time.temporal.ChronoUnit

  val moodleDateTime = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy, h:mm a")
  val myDtFormat = DateTimeFormatter.ofPattern("MMM d yyyy, h a")

  val dueDates =
    Map("hw3" -> LocalDateTime.parse("Feb 11 2016, 1 AM", myDtFormat),
        "hw4" -> LocalDateTime.parse("Feb 23 2016, 1 AM", myDtFormat),
        "hw5" -> LocalDateTime.parse("Mar 1 2016, 1 AM", myDtFormat),
        "hw6" -> LocalDateTime.parse("Mar 8 2016, 1 AM", myDtFormat),
        "hw7" -> LocalDateTime.parse("Apr 5 2016, 1 AM", myDtFormat),
        "hw8" -> LocalDateTime.parse("Apr 12 2016, 1 AM", myDtFormat),
        "hw9" -> LocalDateTime.parse("Apr 19 2016, 1 AM", myDtFormat),
        "hw10" -> LocalDateTime.parse("Apr 26 2016, 1 AM", myDtFormat))

  val moodleGradingSheetRegex = new scala.util.matching.Regex(
    """^Grades-COMPSCI(\d+)-SEC\d+ (FA|SP\d+)-([a-z0-9 ]*)-\d{6}-\d{8}_\d{4}-comma_separated\.csv$""",
    "course", "term", "assignment")

  val lateRegex = new scala.util.matching.Regex("""(?:(\d+) day(?:s)?)?(?: (\d+) hour(?:s)?)?(?: \d+ min(?:s)?)?(?: \d+ sec(?:s)?)? late""", "days", "hours")

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

  def prepareEmail(late: Int, assignments: List[String]): String = {
    """
    |This email is about late homework submitted for COMPSCI220. Recall that the late policy is online:
    |
    |https://people.cs.umass.edu/~arjun/courses/cmpsci220-spring2016/policies/#late
    |
    |According to Moodle, you have used """.stripMargin + late.toString +
    """ late days, which exceeds the four late days you're allowed, so your grade will be affected.
      |If you believe this is an error, please let us know (using Piazza). According to Moodle, you used:
    """.stripMargin  + "\n" +
    assignments.mkString("\n")
  }

  def fromDirectory(dir: String): Unit = {
    import scala.collection.JavaConversions._
    val stream = Files.newDirectoryStream(Paths.get(dir))
    val csvs = stream.filter(p => p.getFileName.toString.endsWith(".csv")).toList
    stream.close()
    val latePeriods = csvs.foldLeft(Map[String, Int]())(processFile)
  }

}
