class TestSuite extends org.scalatest.FunSuite {


  test("foo") {

    val b = new Board(O, Matrix(3, None))
    println(b.minimax)

  }


  test("foo 2") {
    val m = Matrix[Option[Player]](3, None).set(0, 0, Some(O)).set(0, 2, Some(O))
    val b = new Board(O, m)
    println(b.minimax)

  }


  test("foo 3") {
    val m = Matrix[Option[Player]](3, None).set(0, 0, Some(O)).set(0, 2, Some(O))
    val b = new Board(X, m)
    println(b.minimax)

  }


  test("foo 4") {

    val b = new Board(O, Matrix(4, None))
    println(b.minimax)

  }


}