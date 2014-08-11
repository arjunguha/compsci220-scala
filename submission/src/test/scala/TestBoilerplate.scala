import java.nio.file.{Path, Paths, Files}

object TestBoilerplate {

  import scala.collection.JavaConversions._

  def forallFiles(path : String)(block : Path => Unit) : Unit = {
    Files.newDirectoryStream(Paths.get(path)).foreach(block)
  }

}