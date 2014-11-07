package grader

import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardOpenOption.APPEND
import java.io.File
import org.apache.commons.io.FileUtils
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import scala.concurrent.ExecutionContext.Implicits.global

object Grading {

  private val gradeFile = "grading.yaml"

  // Checks to see if all assignments are graded
  def checkGraded(baseDir: String): Unit = {
    import scala.collection.JavaConversions._

    val base = Paths.get(baseDir)

    for (path <- Files.newDirectoryStream(base)
                   .filter(p => Files.isDirectory(p))) {
      if (Files.isRegularFile(path.resolve(gradeFile)) &&
          Files.isRegularFile(path.resolve(".graded"))) {
        // graded
      }
      else if (Files.isRegularFile(path.resolve("compile-error"))) {
        // could not grade
      }
      else {
        println(path)
      }
    }
    println("Any paths listed above are not graded.")
  }

  def fillWorksheet(src: String, dst: String): Unit = {
    import scala.collection.JavaConversions._

    val srcSheet = MoodleSheet(src)
    val dstSheet = srcSheet.fill { submitId =>
      val dir = Paths.get(submitId)
      if (!Files.isDirectory(dir)) {
        (0, "You did not submit or your submission was ill-formed.")
      }
      else if (Files.isRegularFile(dir.resolve("compile-error"))) {
        (0, "Your submission did not compile or you submitted the wrong assignnment.")
      }
      else if (Files.isRegularFile(dir.resolve(gradeFile))) {
        val feedbackStr = new String(Files.readAllBytes(dir.resolve(gradeFile)))
        val feedback = GradingYaml.yaml.load(feedbackStr) match {
          case feedback: FeedbackBean => feedback
        }
        val m = feedback.getCumulative.getScore.toDouble
        val n = feedback.getCumulative.getMaxScore.toDouble
        val score = math.round(m / n * 100.0)
        (score.toInt, feedbackStr)
      }
      else {
        println(ansi().fg(RED).a(s"Skipped submission from $submitId").reset)
        (0, "We did not grade your submission. Contact the course staff.")
      }
    }
    dstSheet.saveAs(Paths.get(dst))
  }
}