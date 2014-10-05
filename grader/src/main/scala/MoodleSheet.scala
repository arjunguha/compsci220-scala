package grader

import java.nio.file._
import com.github.tototoshi.csv._

class MoodleSheet(src: Path) {

   var rows = CSVReader.open(src.toFile).all()

   private def rowById(id: String): Option[List[String]] = rows.tail.find {
    case idCol :: _ => idCol.substring(12)  == id
    case _ => false
   }

   def emailById(id: String): Option[String] = rowById(id).map { row => row(2) }

   def saveAs(dst: Path) = {
     val writer = CSVWriter.open(dst.toFile)
     writer.writeAll(rows)
     writer.close()
   }

}

object MoodleSheet {


  def apply(path: String) = new MoodleSheet(Paths.get(path))

}