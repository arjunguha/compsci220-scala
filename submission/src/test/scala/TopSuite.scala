import TestBoilerplate._
import scala.util.{Success, Failure}
import cs220.submission.sandbox.{Complete, DidNotFinish}
import cs220.submission.{InvalidSubmission, InvalidAssignment}

class TopSuite extends TopFixture {

  test("getting an assignment that exists should succeed") { top =>
    top.getAssignment("asgn1", "step1")
  }

  test("getting a test suite that exists should succeed") { top =>
    assert(top.getTestSuite("asgn1", "step1").length == 2)
  }

  test("ok submission should submit correctly") { top =>
    whenReady(top.checkSubmission("asgn1", "step1", submits.resolve("ok"))) {
      case List(Complete(0, "submission.txt\n", ""),
                Complete(0, "submission.txt\n", "")) => ()
      case other => fail(other.toString)
    }
  }

  test("submision has a missing file") { top =>
    val r = toTry(top.checkSubmission("asgn1", "step1", submits.resolve("missing")))
    whenReady(r) {
      case Failure(exn : InvalidSubmission) => ()
      case other => fail(other.toString)
    }
  }

  test("submit to invalid step") { top =>
    val r = toTry(top.checkSubmission("asgn1", "step-999", submits.resolve("ok")))
    whenReady(r) {
      case Failure(_ : InvalidAssignment) => ()
      case other => fail(other.toString)
    }
  }

  test("submit to invalid assignment") { top =>
    val r = toTry(top.checkSubmission("asgn-999", "step1", submits.resolve("ok")))
    whenReady(r) {
      case Failure(_ : InvalidAssignment) => ()
      case other => fail(other.toString)
    }
  }

}