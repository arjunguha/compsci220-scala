package cmpsci220.grading

import cmpsci220._
import cmpsci220.hw.tictactoe._
import grader.TestSuite

class TicTacToe(targetYaml: String, solution: MinimaxLike) {

  import solution._

  // for tic tac toe, these are whitespace characters that are ignored. They
  // let us write the board in 2D
  val whitespaces = "|\n -".toList

  // A . indicates a blank square
  val validChars = "XO.".toList

  def parse(str: String): Matrix[Option[Player]] = {
    val lst = str.toList.filter(ch => !whitespaces.contains(ch))
    require(lst.length == 9)
    require(lst.forall(ch => validChars.contains(ch)))
    val coords = for (y <- 0.to(2); x <-0.to(2)) yield { (x, y) }
    coords.zip(lst).foldLeft(Matrix[Option[Player]](3, None)) {
      case (matrix, ((x, y), 'X')) => matrix.set(x, y, Some(X))
      case (matrix, ((x, y), 'O')) => matrix.set(x, y, Some(O))
      case (matrix, ((x, y), '.')) => matrix
      case _ => sys.error("should never happen")
    }
  }

  private def myCreateGame(str: String, turn: Player) = {
    val b = parse(str)
    println(b)
    createGame(turn, b)
  }

  TestSuite(targetYaml) { builder =>
    import builder._

    test("Does isFinished produce true on a full board?") {
      val b = myCreateGame("""XOX
                              OXO
                              OXO""", X)
      assert(b.isFinished)
    }

    test("Does isFinished produce false on an empty board?") {
      val b = myCreateGame(".........", X)
      assert(!b.isFinished)
    }

    test("Does getWinner find a winner in a row?") {
      val b = myCreateGame("""O.O
                              XXX
                              .O.""", X)
      assert(b.getWinner == Some(X))
    }

    test("Does getWinner find a winner in a column?") {
      val b = myCreateGame("""OOX
                              XOX
                              .O.""", X)
      assert(b.getWinner == Some(O))
    }

    test("Does getWinner find a winner on the anti-diagonal?") {
      val b = myCreateGame("""OOX
                              OXX
                              X..""", X)
      assert(b.getWinner == Some(X))
    }

    test("Does getWinner find a winner on the  diagonal?") {
      val b = myCreateGame("""XOO
                              XXO
                              ..X""", X)
      assert(b.getWinner == Some(X))
    }

    test("Does nextBoards produce nine next-boards for the empty board?") {
      val b = myCreateGame("""...
                              ...
                              ...""", X)
      assert(b.nextBoards.length == 9)
    }

    test("Does nextBoards produce 3 next-boards when there are three spots left?") {
      val b = myCreateGame("""XOX
                              XOX
                              ...""", O)
      assert(b.nextBoards.length == 3)
    }

    test("Does minimax report draw on the empty board?") {
      val b = myCreateGame("""...
                              ...
                              ...""", X)
      assert(minimax(b) == None)
    }

    test("Does minimax report X as winner on this board (in one move)?") {
      val b = myCreateGame("""XOX
                              OXO
                              .XO""", X)
    }

    test("Does minimax report X as winner on this board (in two moves by X)?") {
      val b = myCreateGame("""X.X
                              .O.
                              X.O""", O)
    }

  }

}
