package grader

import scala.sys.process._
import scala.concurrent._
import scala.concurrent.duration._

object ProcessTimer {

  implicit class RichProcess(process: Process) {

    def withTimeout(n: Int)(implicit ec: ExecutionContext): Option[Int] = {
      try {
        Some(Await.result(Future{process.exitValue}, n.seconds))
      }
      catch {
        case exn:TimeoutException => {
          println("timeout")
          process.destroy
          None
        }
      }
    }
  }


}