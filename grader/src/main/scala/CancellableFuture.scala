package grader

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try

object CancellableFuture {

  def apply[A](body: => A)(implicit ec: ExecutionContext): (Future[A], () => Unit) = {
    val p = Promise[A]()

    val t = new Thread(new Runnable {
      def run(): Unit = {
        try {
          p.success(body)
        }
        catch {
          case exn:Throwable => p.failure(exn)
        }
      }
    })

    def interrupt(): Unit = {
      if (p.isCompleted) {
        return
      }

      t.interrupt()
      p.failure(new InterruptedException("the future was cancelled"))
    }

    t.start()

    (p.future, interrupt)
  }

   // ExecutionException on timeout
  def withTimeout[A](body: () => A, timeout: Duration)
    (implicit ec: ExecutionContext): Try[A] = {
    val (result, cancel) = CancellableFuture(body())

    val t = new Thread(new Runnable {
      def run(): Unit = {
        try {
          Thread.sleep(timeout.toMillis)
          cancel()
        }
        catch {
          case exn:InterruptedException => ()
        }
      }
    })

    t.start()
    Try(Await.result(result andThen { case _ => t.interrupt() }, Duration.Inf))
  }

}