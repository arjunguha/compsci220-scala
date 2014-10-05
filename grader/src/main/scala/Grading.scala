package grader

import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardOpenOption.APPEND
import java.io.File
import org.apache.commons.io.FileUtils
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import scala.concurrent.ExecutionContext.Implicits.global
import java.lang.ProcessBuilder
import com.github.tototoshi.csv._

import ProcessTimer._

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

  private def fillRow(row: List[String]): List[String] = {
    val id = row(0).substring(12)
    val dir = Paths.get(id)
    if (!Files.isDirectory(dir)) {
      row
    }
    else if (Files.isRegularFile(dir.resolve("compile-error"))) {
      row.updated(4, 0.toString) // 0
         .updated(5, 100.toString)
         .updated(7, (new java.util.Date()).toString)
         .updated(8, "Your submission did not compile")
     }
     else if (Files.isRegularFile(dir.resolve(gradeFile))) {
       val feedbackStr = new String(Files.readAllBytes(dir.resolve(gradeFile)))
       val feedback = GradingYaml.yaml.load(feedbackStr) match {
         case feedback: FeedbackBean => feedback
       }
       val m = feedback.getCumulative.getScore.toDouble
       val n = feedback.getCumulative.getMaxScore.toDouble
       val score = math.round(m / n * 100.0)
       row.updated(4, score.toString)
          .updated(5, 100.toString)
          .updated(7, feedback.getTime)
          .updated(8, feedbackStr.replace("\n", "<br>"))
    }
    else {
      println("WARNING: skipped directory $id")
      row
    }
  }

  def fillWorksheet(src: String, dst: String): Unit = {
    import scala.collection.JavaConversions._

    val rows = CSVReader.open(new java.io.File(src)).all()
    val writtenRows = rows.head :: rows.tail.map(fillRow)
    val writer = CSVWriter.open(new java.io.File(dst))
    writer.writeAll(writtenRows)
    writer.close()
  }
}