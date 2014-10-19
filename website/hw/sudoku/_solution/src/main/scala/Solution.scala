import cmpsci220.hw.sudoku._

object Solution extends SudokuLike {

  type T = SudokuBoard

  val oneTo9 = 1.to(9).toSet

  def parse(str: String): SudokuBoard = {
    require(str.length == 81)
    var board = emptyBoard
    for (row <- 0.to(8)) {
      for (col <- 0.to(8)) {
        str(row * 9 + col) match {
          case '.' => ()
          case ch => board.place(row, col, ch.toString.toInt)
        }
      }
    }
    board
  }

  def calcPeers(row: Int, col: Int): Seq[(Int, Int)] = {
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
    }.toSet.toSeq
  }

  val peersTbl = Map((0.to(8).flatMap { r =>
    0.to(8).map { c =>
      ((r, c) -> calcPeers(r, c))
    }
  }) :_*)

  def peers(row: Int, col: Int): Seq[(Int, Int)] = peersTbl((row, col))

  def eliminate(available: Map[(Int, Int), Set[Int]],
                positions: Seq[(Int, Int)],
                value: Int): Map[(Int, Int), Set[Int]] = positions match {
    case Seq() => available
    case Seq(p, rest @ _ *) => {
      val availVals = available.getOrElse(p, oneTo9)
      if (availVals.contains(value)) {
        val set = availVals - value
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
class SudokuBoard(val available: Map[(Int, Int), Set[Int]]) extends BoardLike[SudokuBoard] {
  import Solution._

  val score: Int = available.values.map(_.size).sum

  val isUnsolvable = available.exists { case (_, set) => set.isEmpty }

  val isSolved = available.size == 81 && available.forall { case (_, set) => set.size == 1 }

  def availableValuesAt(row: Int, col: Int): Set[Int] = {
    available.getOrElse((row, col), oneTo9)
  }

  def valueAt(row: Int, col: Int): Option[Int] = {
    availableValuesAt(row, col).toSeq match {
      case Seq(x) => Some(x)
      case _ => None
    }
  }

  def nextStates(): Seq[SudokuBoard] = {
    0.to(8).flatMap { row =>
      0.to(8).flatMap { col =>
        val values = availableValuesAt(row, col)
        if (values.size > 1) {
          values.flatMap { value =>
            val b = place(row, col, value)
            assert(values.size > 1 || b.available == this.available,
                   s"\n$this\nplace($row, $col, $value):\n$b")
                Seq(b)
          }
        }
        else {
          Nil
        }
      }
    }.sortWith((x, y) => x.score < y.score)
  }

  def place(row: Int, col: Int, value: Int): SudokuBoard = {
    assert (availableValuesAt(row, col).contains(value))
    val newAvailable = eliminate(available + ((row, col) -> Set(value)),
                                 peers(row, col),
                                 value)
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
        assert (board.subproblemOf(this))
        board.solve() match {
          case Some(board) => {
            assert (board.subproblemOf(this))
            return Some(board)
          }
          case None => ()
        }
      }
      return None
    }
  }

  def subproblemOf(parent: SudokuBoard): Boolean = {
    0.to(8).forall { row =>
      0.to(8).forall { col =>
        availableValuesAt(row, col).subsetOf(parent.availableValuesAt(row, col))
      }
    }
  }
}
