import org.scalatest._
import Solution._

class TestSuite extends FunSuite {

  // test("all cells have 20 peers") {
  //   for (row <- 0.to(8)) {
  //     for (col <- 0.to(8)) {
  //       // 8 row-peers + 8 col-peers + 4 box peers
  //       assert(peers(row, col).toSet.size == 20, s"peers($row, $col)")
  //     }
  //   }
  // }

  def solveSudoku(txt: String) = {
    test(txt) {
      val board = parse(txt).solve
      assert(board != None, txt)
      println(board)
    }
  }

  // These are from CS121
  solveSudoku("85....4.1......67...21....3..85....7...982...3....15..5....43...37......2.9....58")
  solveSudoku(".1.....2..3..9..1656..7...33.7..8..........89....6......6.254..9.5..1..7..3.....2")
  // solveSudoku("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.")

  solveSudoku(".43.8.25.6.............1.949....4.7....6.8....1.2....382.5.............5.34.9.71.")
  solveSudoku("2...8.3...6..7..84.3.5..2.9...1.54.8.........4.27.6...3.1..7.4.72..4..6...4.1...3")
  // solveSudoku("..3.2.6..9..3.5..1..18.64....81.29..7.......8..67.82....26.95..8..2.3..9..5.1.3..")
  // solveSudoku("1..92....524.1...........7..5...81.2.........4.27...9..6...........3.945....71..6")

  val allPositions = for (r <- 0.to(8); c <- 0.to(8)) yield { (r, c) }

  // Produces true if the board does not exclude the given solution
  private def canFindSolution(solutionStr: String) = {
    val sol = solutionStr.toArray
    (board: Board) => {
      !board.isUnsolvable &&
      allPositions.forall { case (r, c) =>
        sol(r * 9 + c) match {
          case '.' => true
          case ch =>  board.valueAt(r, c) match {
            case Some(v) => v == ch.toString.toInt
            case None => board.availableValuesAt(r, c).toSet.contains(ch.toString.toInt)
          }
        }
      }
    }
  }

  type Board = SudokuBoard

  def sudokuOracle(board: Board, containsSolution: Board => Boolean): Boolean = {
    if (board.isSolved) {
      containsSolution(board)
    }
    else {
      board.nextStates.find(containsSolution) match {
        case None => {
          println("here")
          false
        }
        case Some(b) => sudokuOracle(b, containsSolution)
      }
    }
  }

  def guidedSearch(puzzle: String, filled: String): Unit = {
    val check = canFindSolution(filled)
    val board = parse(puzzle)
    assert(sudokuOracle(board, check))
  }

  test("guided 1") {
    guidedSearch(".43.8.25.6.............1.949....4.7....6.8....1.2....382.5.............5.34.9.71.",
                 "245981376169273584837564219976125438513498627482736951391657842728349165654812793")
  }

}