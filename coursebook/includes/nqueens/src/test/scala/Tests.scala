class Tests extends org.scalatest.FunSuite {

  test("NaiveQueens(8, Set())") {
    val board = new NaiveQueens(8, Set())
    assert(board.solve != None)
  }

  test("NaiveQueens(11, Set())") {
    val board = new NaiveQueens(11, Set())
    assert(board.solve != None)
  }

  test("OptQueens(18, Set())") {
    val board = OptQueens.empty(18)
    assert(board.solve != None)
  }

}