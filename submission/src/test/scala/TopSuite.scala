import TestBoilerplate._
import scala.util.{Success, Failure}
import cs220.submission.sandbox.{Complete, DidNotFinish}
import cs220.submission.{InvalidSubmission, InvalidAssignment, TestResult}
import Rich._

class TopSuite extends TopFixture {

  test("getting an assignment that exists should succeed") { top =>
    top.getAssignment("asgn1", "step1")
  }

  test("getting a test suite that exists should succeed") { top =>
    assert(top.getTestSuite("asgn1", "step1").length == 2)
  }

  test("ok submission should submit correctly") { top =>
    whenReady(toTry(top.checkSubmission("asgn1", "step1", submits.resolve("ok")).futureSeq)) {
      case Success(Seq(TestResult(_, Complete(0, "submission.txt\nyamltest\n", "")),
                TestResult(_, Complete(0, "submission.txt\nyamltest\n", "")))) => ()
      case other => fail(other.toString)
    }
  }

  test("submision has a missing file") { top =>
    whenReady(toTry(top.checkSubmission("asgn1", "step1", submits.resolve("missing")).futureSeq)) {
      case Failure(_ : InvalidSubmission) => ()
      case other => fail(other.toString)
    }
  }

  test("submit to invalid step") { top =>
    whenReady(toTry(top.checkSubmission("asgn1", "step-999", submits.resolve("ok")).futureSeq)) {
      case Failure(_ : InvalidAssignment) => ()
      case other => fail(other.toString)
    }
  }

  test("submit to invalid assignment") { top =>
    whenReady(toTry(top.checkSubmission("asgn-999", "step1", submits.resolve("ok")).futureSeq)) {
      case Failure(_ : InvalidAssignment) => ()
      case other => fail(other.toString)
    }
  }

}