package grader

import java.nio.file._
import com.github.tototoshi.csv._

/** For scripting Moodle grade sheets. Assumes "Simple directed grading"
 * with a maximum grade of 100 and Feedback comments enabled".
 */
class MoodleSheet private (rows: List[List[String]]) {
  import MoodleSheet._

  require(rows.length > 0 &&
          rows.head == expectedHeader,
    s"the header row has unexpected columns. Expected $expectedHeader")


   private def rowById(id: String): Option[List[String]] = rows.tail.find {
     case idCol :: _ => idCol.substring(12)  == id
     case _ => false
   }

   private def rowByEmail(email: String): Option[List[String]] = rows.tail.find {
     case _ :: _ :: emailCol :: _ => emailCol == email
     case _ => false
   }

   def idByEmail(email: String): Option[String] = rowByEmail(email).map { row => row(0).substring(12) }

   def emailById(id: String): Option[String] = rowById(id).map { row => row(2) }

   def saveAs(dst: Path) = {
     val writer = CSVWriter.open(dst.toFile)
     writer.writeAll(rows)
     writer.close()
   }

   lazy val submissionIds: List[String] = rows.tail.map(_(0).substring(12))

   /** Applies the supplied {@code grade} function to every student on the
       sheet. */
   def fill(grade: String => (Int, String)): MoodleSheet = {
     val now = new java.util.Date().toString
     val header = rows.head
     val data = for (row <- rows.tail) yield {
       val id = row(0).substring(12)
       val (score, feedback) = grade(id)
       require (score >= 0 && score <= 100)
       row.updated(4, score.toString)
          .updated(7, now)
          .updated(8, feedback.replace("\n", "<br>"))
    }
    new MoodleSheet(header :: data.toList)
   }
}

object MoodleSheet {

  private val expectedHeader = List("Identifier",
                                    "Full name",
                                    "Email address",
                                    "Status",
                                    "Grade",
                                    "Maximum grade",
                                    "Last modified (submission)",
                                    "Last modified (grade)",
                                    "Feedback comments")


  def apply(path: String): MoodleSheet = MoodleSheet(Paths.get(path))

  def apply(path: Path): MoodleSheet = {
    new MoodleSheet(CSVReader.open(path.toFile).all())
  }

}