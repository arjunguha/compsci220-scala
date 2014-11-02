import cmpsci220.hw.sudoku._

object Solution extends SudokuLike {

  type T = SudokuBoard

  val oneTo9 = 1.to(9).toList

  def parse(str: String): SudokuBoard = {
    require(str.length == 81)
    var board = emptyBoard
    for (row <- 0.to(8); col <- 0.to(8)) {
      str(row * 9 + col) match {
        case '.' => ()
        case ch => board = board.place(row, col, ch.toString.toInt)
      }
    }
    board
  }

  def calcPeers(row: Int, col: Int): List[(Int, Int)] = {
    val rowPeers = 0.to(8).map { r => (r,col) }
    val colPeers = 0.to(8).map { c => (row, c) }
    val boxRow: Int = (row / 3) * 3
    val boxCol: Int = (col / 3) * 3
    val boxPeers = boxRow.to(boxRow + 2).flatMap { r =>
      boxCol.to(boxCol + 2).map { c =>
        (r, c)
      }
    }
    (rowPeers ++ colPeers ++ boxPeers).filterNot {
      case (r, c) => r == row && col == c
    }.toList.distinct
  }

  val peersTbl = Map((0.to(8).flatMap { r =>
    0.to(8).map { c =>
      ((r, c) -> calcPeers(r, c))
    }
  }) :_*)

  def peers(row: Int, col: Int): Seq[(Int, Int)] = peersTbl((row, col))

  def eliminate(available: Map[(Int, Int), List[Int]],
                positions: Seq[(Int, Int)],
                value: Int): Map[(Int, Int), List[Int]] = positions match {
    case Seq() => available
    case Seq(p, rest @ _ *) => {
      val availVals = available.getOrElse(p, oneTo9)
      if (availVals.contains(value)) {
        val set = availVals.filter(_ != value)
        val avail1 = available + (p -> set)
        val avail2 = if (set.size == 1) {
          val v = set.head
          // println(s"$p -> $v")
          eliminate(avail1, peers(p._1, p._2), set.head)
        }
        else {
          avail1
        }
        eliminate(avail2, rest, value)
      }
      else {
        eliminate(available, rest, value)
      }
    }
  }


  val emptyBoard = new SudokuBoard(available = Map())

}

// // Top-left corner is (0,0). Bottom-right corner is (8,8).
class SudokuBoard(val available: Map[(Int, Int), List[Int]]) extends BoardLike[SudokuBoard] {
  import Solution._

  val score: Int = available.values.map(_.size).sum

  val isUnsolvable = available.exists { case (_, set) => set.isEmpty }

  val isSolved = available.size == 81 && available.forall { case (_, set) => set.size == 1 }

  def availableValuesAt(row: Int, col: Int): List[Int] = {
    available.getOrElse((row, col), oneTo9)
  }

  def valueAt(row: Int, col: Int): Option[Int] = {
    availableValuesAt(row, col).toSeq match {
      case Seq(x) => Some(x)
      case _ => None
    }
  }

  def nextStates(): Iterable[SudokuBoard] = {
    val nexts = for (row <- 0.to(8);
                     col <- 0.to(8);
                     value <- availableValuesAt(row, col);
                     if availableValuesAt(row, col).size > 1) yield {
      place(row, col, value)
    }
    nexts.sortWith((x, y) => x.score < y.score)
  }

  def place(row: Int, col: Int, value: Int): SudokuBoard = {
    assert (availableValuesAt(row, col).contains(value))
    val newAvailable = eliminate(available + ((row, col) -> List(value)),
                                 peers(row, col),
                                 value)
    val s = newAvailable((row, col))
    //assert (s == Set(value) || s.isEmpty, s"expected Set($value), got ${newAvailable((row, col))}")
    new SudokuBoard(newAvailable)
  }

  def solve(): Option[SudokuBoard] = {
    if (isSolved) {
      Some(this)
    }
    else if (isUnsolvable) {
      None
    }
    else {
      for (board <- nextStates()) {
        board.solve() match {
          case Some(board) => {
            return Some(board)
          }
          case None => ()
        }
      }
      return None
    }
  }

  // def subproblemOf(parent: SudokuBoard): Boolean = {
  //   0.to(8).forall { row =>
  //     0.to(8).forall { col =>
  //       availableValuesAt(row, col).subsetOf(parent.availableValuesAt(row, col))
  //     }
  //   }
  // }
}

object Main extends App {

 val puz = "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4."
 val board = Solution.parse(puz)
 println(s"Trying to solve:\n$board\n-----------------")
 println(board.solve())
}