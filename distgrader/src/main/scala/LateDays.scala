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
        "hw9" -> LocalDateTime.parse("Apr 19 2016, 1 AM", myDtFormat))

  val moodleGradingSheetRegex = new scala.util.matching.Regex(
    """^Grades-COMPSCI(\d+)-SEC\d+ (FA|SP\d+)-([a-z0-9 ]*)-\d{6}-\d{8}_\d{4}-comma_separated\.csv$""",
    "course", "term", "assignment")

  // Maps each student to the number of days late
  def processFile(filename: Path): Map[String, (Int, List[String])] = {
    moodleGradingSheetRegex.findFirstMatchIn(filename.getFileName.toString) match {
      case None => {
        println(filename.getFileName.toString)
        ???
      }
      case Some(regexMatch) => {
        val assignment = regexMatch.group("assignment")
        val deadline = dueDates(assignment)
        val times: Map[String, LocalDateTime] = CSV.fromFile(filename).tail.filterNot(row => row(3).contains("No submission"))
          .map(row => row(2) -> LocalDateTime.parse(row(7), moodleDateTime)).toMap

        def latePeriods(submitTime: LocalDateTime): Int = {
          val lateHours = ChronoUnit.HOURS.between(deadline, submitTime).toInt
          if (lateHours > 1) {
            lateHours / 24 + (if (lateHours % 24 == 0) 0 else 1)
          }
          else {
            0
          }
        }
        times.mapValues(latePeriods).filter(_._2 > 0).mapValues(n => (n, List(s"$n for $assignment")))
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
    val maps = csvs.map(processFile)
    def f(x: (Int, List[String]), y: (Int, List[String])) = (x._1 + y._1, x._2 ++ y._2)
    val r = maps.reduce((x, y) => x.combine(y)(f, identity, identity)).filter(_._2._1 > 4)
    for ((name, (periods, assignments)) <- r) {
      sendEmail(to = name, subject = "COMPSCI220 -- Late homework",
                body = prepareEmail(periods, assignments))
      println(s"$name -- $periods $assignments")
    }
  }

}
