import lectures.constraintpropagation._

import org.scalatest._

class ConstraintProgrammingSuite extends FunSuite {

  test("8 queens") {

    println(ChessBoard.emptyBoard(8).solve.get)

  }

  test("9 queens") {

    println(ChessBoard.emptyBoard(9).solve.get)

  }

  test("20 queens") {

    println(ChessBoard.emptyBoard(20).solve.get)

  }

}

