
class TestSuite extends org.scalatest.FunSuite {

  def canParse(str: String): Boolean = {
    !ArithParser.parseAll(ArithParser.expr, str).isEmpty
  }

  test("test 1") {
    assert(canParse("12"))
  }


  test("test 2") {

    assert(canParse("10 + 4"))
  }

}