import org.scalatest.{FunSuite, Matchers}
import cmpsci220._
import cmpsci220.hw.measurement._

class RegressionSuite extends FunSuite with Matchers{

  test("trivial two-point test") {
    assert(linearRegression(List((0, 0), (10, 10))) == (1, 0, 1))
  }

  test("trivial three-point test with error") {
    val (slope, intercept, rSq) = linearRegression(List((0, 0), (10, 10), (100, 95)))
    slope  should be (1.0 +- 0.1)
    intercept should be (0.0 +- 0.3)
    rSq should be (1.0 +- 0.1)
  }

  test("log regression ") {
    val (slope, intercept, rSq) = logRegression(List((1, 1), (2, 10), (4, 20), (8, 30), (16, 40)))
    rSq should be (1.0 +- 0.01)
  }




}