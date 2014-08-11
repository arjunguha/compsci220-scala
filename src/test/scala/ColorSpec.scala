package cmpsci220.graphics

import org.scalatest.FunSuite

class ColorSuite extends FunSuite {

  test("two copies of the same custom color should be equal") {
    val c1 = rgb(0.5, 0.5, 0.5)
    val c2 = rgb(0.5, 0.5, 0.5)
    assert (c1 == c2)
  }

  test("the named color blue should be equal to a constructed blue") {
    assert (blue == rgb(0, 0, 1))
  }

  test("a red value > 1 signals an IllegalArgumentException") {
    intercept[IllegalArgumentException] {
      rgb(1.1, 0, 0)
    }
  }

  test("a green value < 0 signals an IllegalArgumentException") {
    intercept[IllegalArgumentException] {
      rgb(0, -0.1, 0)
    }
  }

  test("an rgb color should pretty-print ") {
    assert(rgb(.1, .2, .3).toString == "rgb(0.1, 0.2, 0.3)")
  }
}