import Lists._

class TestSuite extends org.scalatest.FunSuite {

  test("oddNumbers properly defined") {
    assert(oddNumbers == List(1, 3, 5))
  }

}