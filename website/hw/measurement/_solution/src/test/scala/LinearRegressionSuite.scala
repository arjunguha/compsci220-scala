import cmpsci220._
import cmpsci220.hw.measurement._
import Solution._

class LinearRegressionSuite extends org.scalatest.FunSuite {

  def isNonZeroTime(pt: (Double, Double)): Boolean = pt._2 != 0

  test("timing insertAllBST on ordered input") {
    // You may need to tweak this data to suit your computer
    val data = map((x: Int) => revOrder(math.pow(2, x + 1).toInt), revOrder(10))
    val timing = map(timeInsertAll(insertAllBST, 5), data)

    val line = linearRegression(timing)
    assert(line.rSquared >= 0.85)
  }

  test("timing insertAllAVL on random input") {
    // You may need to tweak this data to suit your computer
    val data = map((x: Int) => revOrder(math.pow(2, x + 1).toInt), revOrder(14))

    val timing = map(timeInsertAll(insertAllAVL, 5), data)
    // Remove points with 0 as the y-coordinate, so that log is defined
    val zeroesRemoved = filter(isNonZeroTime, timing)
    // Put the X-axis on a log scale, so that we can fit a line
    val logScaleX = map((xy: (Double, Double)) => (math.log(xy._1), xy._2), zeroesRemoved)

    val line = linearRegression(logScaleX)
    assert(line.rSquared >= 0.85)
  }

}