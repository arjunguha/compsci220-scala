import hw.tictactoe._

class GradingTests extends org.scalatest.FunSuite {

  import Solution._

  // for tic tac toe, these are whitespace characters that are ignored. They
  // let us write the board in 2D
  val whitespaces = "|\n\r -".toList

  // A . indicates a blank square
  val validChars = "XO.".toList

  def parse(str: String): (Map[(Int, Int), Player], Int) = {
    val lst = str.toList.filter(ch => !whitespaces.contains(ch))
    val dim = math.sqrt(lst.length).toInt
    require(dim >= 3, "dimension error")
    require(lst.forall(ch => validChars.contains(ch)) , s"invalid characters in $lst")
    val coords = for (y <- 0.until(dim); x <-0.until(dim)) yield { (x, y) }
    val board = coords.zip(lst).foldLeft(Map[(Int, Int), Player]()) {
      case (matrix, ((x, y), 'X')) => matrix + ((x, y) -> X)
      case (matrix, ((x, y), 'O')) => matrix + ((x, y) -> O)
      case (matrix, ((x, y), '.')) => matrix
      case _ => sys.error("should never happen")
    }
    (board, dim)
  }

  private def myCreateGame(str: String, turn: Player) = {
    val (b, dim) = parse(str)
    createGame(turn, dim, b)
  }

  test("Does isFinished produce true on a full board?") {
    val b = myCreateGame("""XOX
                            OXO
                            OXO""", X)
    assert(b.isFinished)
  }

  test("Does isFinished produce false on an empty 3x3 board?") {
    val b = myCreateGame(".........", X)
    assert(!b.isFinished)
  }

  test("Does isFinished produce false on an empty 4x4 board?") {
    val b = myCreateGame("." * 16, X)
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

  test("Does getWinner find a winner on the diagonal?") {
    val b = myCreateGame("""XOO
                            XXO
                            ..X""", X)
    assert(b.getWinner == Some(X))
  }

  test("Does getWinner find a winner in a row on a 4x4 board?") {
    val b = myCreateGame("""O.O.
                            XXXX
                            .O..
                            ....""", X)
    assert(b.getWinner == Some(X))
  }

  test("Does nextBoards produce nine next-boards for the empty board?") {
    val b = myCreateGame("""...
                            ...
                            ...""", X)
    assert(b.nextBoards.length == 9)
  }

  test("Does nextBoards project 16 next-boards for the empty 4x4 board?") {
    val b = myCreateGame("." * 16, X)
    assert(b.nextBoards.length === 16)
  }

  test("Does nextBoards produce 3 next-boards when there are three spots left?") {
    val b = myCreateGame("""XOX
                            XOX
                            ...""", O)
    assert(b.nextBoards.length == 3)
  }

  test("Does nextBoards produce 4 next-boards when there are four spots left on a 4x4 board?") {
    val b = myCreateGame("""XOXO
                            XOXO
                            XOXO
                            ....""", O)
    assert(b.nextBoards.length == 4)
  }

  test("Does minimax report draw on the empty board?") {
    val b = myCreateGame("""...
                            ...
                            ...""", X)
    assert(minimax(b) == None)
  }

  test("Does minimax report X as winner on a board (in one move by X)?") {
    val b = myCreateGame("""XOX
                            OXO
                            .XO""", X)
  }

  test("Does minimax report X as winner on a board (in two moves by X)?") {
    val b = myCreateGame("""X.X
                            .O.
                            X.O""", O)
  }

}
