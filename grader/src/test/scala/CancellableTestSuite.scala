import grader._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class CancellableTestSuite extends org.scalatest.FunSuite {

  test("thread can be interrupted") {

    def forever(): Unit = {
      while(true) { }
    }

    println(CancellableFuture.withTimeout(forever, 3.seconds))
  }

}