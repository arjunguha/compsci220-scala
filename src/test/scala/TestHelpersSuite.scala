import org.scalatest.FunSuite

class TestHelpersSuite extends FunSuite {

  test("The output above should look ok (manually inspect)") {
    // Although .test is ambiguous, this imports the implicit
    import cmpsci220._

    cmpsci220.test("this is green") {
      // do nothing
    }

    cmpsci220.test("this is red") {
      throw new IllegalArgumentException("this is gray")
    }

    cmpsci220.fails("this is green") {
      throw new IllegalArgumentException("this is gray")
    }

    cmpsci220.fails("this is red") {
      // do nothing
    }

  }

}