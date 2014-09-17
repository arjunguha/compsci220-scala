import org.scalatest.{FunSuite, Matchers}
import cmpsci220._
import cmpsci220.hw.measurement._

class RegressionSuite extends FunSuite with Matchers{

  test("trivial two-point test") {
    assert(linearRegression(List((0, 0), (10, 10))) == LinearRegressionResult(1, 0, 1))
  }

  test("trivial three-point test with error") {
    val r = linearRegression(List((0, 0), (10, 10), (100, 95)))
    r.slope should be (1.0 +- 0.1)
    r.intercept should be (0.0 +- 0.3)
    r.rSquared should be (1.0 +- 0.1)
  }


}