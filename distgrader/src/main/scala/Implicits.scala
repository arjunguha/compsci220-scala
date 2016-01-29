package grading


object Implicits {

  import com.ning.http.client.ListenableFuture
  import scala.concurrent.{ExecutionContext, Future}
  import java.nio.file._

  import scala.language.implicitConversions

  implicit class RichListenableFuture[A](listenableFuture: ListenableFuture[A]) {

    def future(implicit ec: ExecutionContext): Future[A] = Future(listenableFuture.get())

  }

}
