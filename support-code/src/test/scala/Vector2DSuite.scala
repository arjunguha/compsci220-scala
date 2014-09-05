import cmpsci220.hw.pong._
import org.scalatest._

class Vector2DSuite extends FunSuite with Matchers {

  test("testing vector addition") {
    assert(Vector2D(100, 50) + Vector2D(200, 30) == Vector2D(300, 80))
  }

  test("distance zero test") {
    val v = Vector2D(100, 20)
    assert(v.distance(v) == 0)
  }

  test("3-4-5 distance test") {
    assert(Vector2D(0, 0).distance(Vector2D(3,4)) == 5)
  }

  test("30-60-90 angle test") {
    val p1 = Vector2D(0, 0)
    val p2 = Vector2D(1, 0)
    val p3 = Vector2D(1, math.sqrt(3))
    val a1 =
    math.toDegrees(p1.angle(p2, p3)) should be (60.0 +- 0.1)
    math.toDegrees(p2.angle(p3, p1)) should be (90.0 +- 0.1)
    math.toDegrees(p3.angle(p2, p1)) should be (30.0 +- 0.1)
  }

}