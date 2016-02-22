package grading

object GradeDiscussion1 {

  import scala.concurrent._
  import scala.concurrent.duration._
  import java.nio.file._

  def main(): Unit = {

    val scripting = new grading.Scripting("10.8.0.6")
    import scripting._
    import scripting.system.dispatcher

    def testBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
      zip.add(s"object GradingMain extends App { import Exercises._; $body }".getBytes,
        "src/main/scala/GradingMain.scala")
    }

    val dir = "discussion1"
    val lst = Scripting.assignments(dir).map(dir => {
      updateState(dir.resolve("grading.json")) { case rubric =>

        val rubric_ = rubric

        SBTTesting.testWithSbt(scripting, dir, testBuilder, rubric_) {  case root =>
          val compiles = root.thenCompile("Check that object Exercises is defined", "()")


          val exercise1 =  compiles.thenCompile("Does exercise1 have the right type?",
             """def foo(lst: List[Bird]): List[String] = exercise1(lst)""", score = 0)

          exercise1.thenRun("Does exercise1 work on the empty list?",
            """assert(exercise1(Nil) == Nil)""", score = 0)

          exercise1.thenRun("Does exercise1 work on the given example?",
            """assert(exercise1(List(Duck(), Duck(), Goose())) == List("dog food", "dog food", "pate"))""", score = 0)

          exercise1.thenRun("Does exercise1 work on a different example?",
            """assert(exercise1(List(Duck(), Duck(), Goose(), Goose(), Goose())) == List("dog food", "dog food", "pate", "pate", "pate"))""", score = 0)

        }
      }
    })

    val result = Await.result(Future.sequence(lst), Duration.Inf)
    println("Grading jobs complete")

    val gradeRegex = """Percentage: (\d+)%""".r
    val grades = MoodleSheet(s"$dir/moodle.csv").fill(id => {
      val reportPath = Paths.get(s"$dir/$id/report.txt")
      if (Files.exists(reportPath)) {
        val feedback = new String (Files.readAllBytes(reportPath))
        val grade = gradeRegex.findFirstMatchIn(feedback).get.group(1).toInt
        (grade, feedback)
      }
      else {
        (0, "Late submission. Will be graded later")
      }
    })
    grades.saveAs(s"$dir/moodle.csv")
    println("Done grading")
    scripting.system.terminate()

  }

}
