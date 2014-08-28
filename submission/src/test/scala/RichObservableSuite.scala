import scala.util.{Try, Success, Failure}
import scala.concurrent._
import rx.lang.scala._
import Rich._
import TestBoilerplate._

class RichObservableSuite extends RichObservableFixture {

  test("futureSeq of a list") {
    whenReady(Observable.from(Seq(1, 3, 5, 7)).futureSeq) {
      case Seq(1, 3, 5, 7) => ()
      case other => fail(other.toString)
    }
  }

  test("futureSeq of an empty list") {
    whenReady(Observable.from(Seq[Int]()).futureSeq) {
      case Seq() => ()
      case other => fail(other.toString)
    }
  }

  test("futureSeq of Observable.error") {
    val exn = new IllegalArgumentException("the exception")
    whenReady(toTry(Observable.error(exn).futureSeq)) {
      case Failure(exn2) => assert(exn == exn2)
      case other => fail(other.toString)
    }
  }

}