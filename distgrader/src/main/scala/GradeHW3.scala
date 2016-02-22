package grading

object GradeHW3 {

  import scala.concurrent._
  import scala.concurrent.duration._
  import Messages._
  import edu.umass.cs.zip._
  import java.nio.file._

  def main(): Unit = {

    val scripting = new grading.Scripting("10.8.0.6")
    import scripting._
    import scripting.system.dispatcher

    def testBuilder(zip: ZipBuilder, body: String): Unit = {
      zip.add("""
           libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.0"
           """.getBytes,
        "build.sbt")
      zip.add(Paths.get("cdc-life-expectancy.csv"), "cdc-life-expectancy.csv")
      zip.add("""
           package edu.umass.cs {
             object CSV {
               import java.nio.file._
               import com.github.tototoshi.csv._

               def fromFileReal(path: Path): List[List[String]] = {
                 val reader = CSVReader.open(path.toFile)
                 try {
                   reader.all()
                 }
                 finally {
                   reader.close()
                 }
               }

               def fromFile(path: String): List[List[String]] = {
                 if (path == "cdc-life-expectancy.csv") {
                   fromFileReal(Paths.get("cdc-life-expectancy.csv"))
                 }
                 else {
                   List()
                 }
               }
               def fromFile(path: Path): List[List[String]] = List()
               def fromString(csvString: String): List[List[String]] = List()
             }
           }
           """.getBytes,
        "src/main/scala/CSV.scala")
      zip.add(
        s"object GradingMain extends App { import Homework3._; $body }".getBytes,
        "src/main/scala/GradingMain.scala")
    }

    val lst = assignments("hw3").map(dir => {
      updateState(dir.resolve("grading.json")) { case report =>


        SBTTesting.testWithSbt(scripting, dir, testBuilder, report) {  case root =>
          val compiles = root.thenCompile("Check that object Homework3 is defined", "()")


        }
      }
    })

    val result = Await.result(Future.sequence(lst), Duration.Inf)
    println("Grading jobs complete")

//    val gradeRegex = """Percentage: (\d+)%""".r
//    val grades = MoodleSheet("hw2/moodle.csv").fill(id => {
//      val reportPath = Paths.get(s"hw2/$id/report.txt")
//      if (Files.exists(reportPath)) {
//        val feedback = new String (Files.readAllBytes(reportPath))
//        val grade = gradeRegex.findFirstMatchIn(feedback).get.group(1).toInt
//        (grade, feedback)
//      }
//      else {
//        (0, "Late submission. Will be graded later")
//      }
//    })
//    grades.saveAs("hw2/moodle.csv")
//    println("Done grading")
//    scripting.system.terminate()

  }

}
