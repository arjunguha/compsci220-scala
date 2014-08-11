import java.nio.file.{Path, Paths, Files}
import scala.util.{Try, Success, Failure}
import scala.concurrent._

object TestBoilerplate {

  import scala.collection.JavaConversions._

  def forallFiles(path : String)(block : Path => Unit) : Unit = {
    Files.newDirectoryStream(Paths.get(path)).foreach(block)
  }

  def toTry[T](future : Future[T])
    (implicit ec : ExecutionContext) : Future[Try[T]] = {
    future.map({ v : T => Success(v) })
          .recover({ case t : Throwable => Failure[T](t) : Try[T] })
  }

}