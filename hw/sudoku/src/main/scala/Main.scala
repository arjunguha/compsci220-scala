import hw.sudoku._

object Solution extends SudokuLike {

  type T = SudokuBoard

  def calcAllPos(ix: Int): List[(Int, Int)] = {
    if (ix == 81) Nil else (ix / 9, ix % 9) :: calcAllPos(ix + 1)
  }

  val allPos = calcAllPos(0)

  def parseHelper(alist: List[(Char, (Int, Int))]): SudokuBoard = alist match {
    case Nil => emptyBoard
    case ('.', _) :: rest => parseHelper(rest)
    case (digit, (row, col)) :: rest => {
      val n = digit.toString.toInt
      parseHelper(rest).place(row, col, n)
    }
  }

  def parse(str: String): SudokuBoard = parseHelper(str.toList.zip(allPos))

  def calcPeers(row: Int, col: Int): List[(Int, Int)] = {
    val rowPeers = 0.to(8).map(r => (r,col))
    val colPeers = 0.to(8).map(c => (row, c))
    val boxRow = (row / 3) * 3
    val boxCol = (col / 3) * 3
    val boxPeers = boxRow.to(boxRow + 2).flatMap(r =>
      boxCol.to(boxCol + 2).map(c => (r, c)))
    // Remove duplicates and (row, col)
    (rowPeers ++ colPeers ++ boxPeers).toSet.diff(Set((row, col))).toList
  }

  val peersTbl = allPos.map(pos => {
    val (row, col) = pos
    pos -> calcPeers(row, col)
  }).toMap

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

  val oneTo9 = 1.to(9).toList
  val emptyBoard = new SudokuBoard(allPos.map(coord => coord -> oneTo9).toMap)

}

// // Top-left corner is (0,0). Bottom-right corner is (8,8).
class SudokuBoard(val available: Map[(Int, Int), List[Int]]) extends BoardLike[SudokuBoard] {
  import Solution._

  val score: Int = available.values.map(_.size).sum

  val isUnsolvable = available.exists { case (_, set) => set.isEmpty }

  val isSolved = available.size == 81 && available.forall { case (_, set) => set.size == 1 }

  override def toString(): String = {
    if (!isSolved) {
      super.toString()
    }
    else {
      val strs = for (row <- 0.to(8); col <- 0.to(8)) yield { valueAt(row, col).get.toString }
      strs.mkString
    }
  }

  def availableValuesAt(row: Int, col: Int): List[Int] = {
    available.get((row, col))
  }

  def valueAt(row: Int, col: Int): Option[Int] = {
    availableValuesAt(row, col).toSeq match {
      case Seq(x) => Some(x)
      case _ => None
    }
  }

  def nextStates(): List[SudokuBoard] = {
    val nexts = for (row <- 0.to(8);
                     col <- 0.to(8);
                     value <- availableValuesAt(row, col);
                     if availableValuesAt(row, col).size > 1) yield {
      place(row, col, value)
    }
    nexts.sortWith((x, y) => x.score < y.score).toList
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

  def solveRec(alist: List[SudokuBoard]): Option[SudokuBoard] = alist match {
    case Nil => None
    case head :: tail => head.solve() match {
      case Some(result) => Some(result)
      case None => solveRec(tail)
    }
  }
  def solve(): Option[SudokuBoard] = {
    if (isSolved) {
      Some(this)
    }
    else if (isUnsolvable) {
      None
    }
    else {
      solveRec(nextStates())
    }
  }

}
