object SudokuBoard {

  var counter = 0

   val oneToTen = 1.to(9).toSet

  def peers(row: Int, col: Int): Seq[(Int, Int)] = {
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
    }
  }

  def eliminate(available: Map[(Int, Int), Set[Int]],
                positions: Seq[(Int, Int)],
                value: Int): Map[(Int, Int), Set[Int]] = positions match {
    case Seq() => available
    case Seq(p, rest @ _ *) => {
      val availableAt = available.getOrElse(p, oneToTen) - value
      eliminate(available + (p -> availableAt), rest, value)
    }
  }


  val emptyBoard = new SudokuBoard(available = Map())


  def init(places: List[((Int, Int), Int)]): Option[SudokuBoard] = places match {
    case Nil => Some(emptyBoard)
    case ((row, col), value) :: rest =>
      init(rest).flatMap(board => board.place(row, col, value))
  }

}
// // Top-left corner is (0,0). Bottom-right corner is (8,8).
class SudokuBoard(available: Map[(Int, Int), Set[Int]]) {

  import SudokuBoard._

  def score(): Int = {
    available.values.map(_.size).sum
  }

  val isUnsolvable = available.exists { case (_, set) => set.isEmpty }

  val isSolved = {
    available.size == 81 && available.forall { case (_, set) => set.size == 1 }
  }

  def nextStates(): Seq[SudokuBoard] = {
    val nexts = 0.to(8).flatMap { row =>
      0.to(8).flatMap { col =>
        available.getOrElse((row, col), oneToTen).flatMap { value =>
          place(row, col, value).toList
        }
      }
    }
    nexts.sortWith((x, y) => x.score < y.score)
  }


  override def toString(): String = {
    if (!isSolved) {
      val b = new StringBuilder()
      for (r <- 0.to(8)) {
        for (c <- 0.to(8)) {
          b.append(available.getOrElse((r, c), oneToTen).mkString.padTo(10, " ").mkString)
        }
        b += '\n'
      }
      b.toString
    }
    else {
      val b = new StringBuilder()
      for (r <- 0.to(8)) {
        for (c <- 0.to(8)) {
          b.append(available.getOrElse((r, c), oneToTen).mkString)
        }
        b += '\n'
      }
      b.toString
    }
  }


  def place(row: Int, col: Int, value: Int): Option[SudokuBoard] = {
    assert(row >= 0 && row <= 8)
    assert(col >= 0 && col <= 8)
    assert(value >= 1 && value <= 9)

    if (!available.getOrElse((row, col), oneToTen).contains(value)) {
      // println(s"Cannot place ($row, $col) -> $value")
      // println(this)
      // println(available.getOrElse((row, col), oneToTen))
      // println(peers(row, col))
      None
    }
    else {
      val newAvailable = eliminate(available, peers(row, col), value)
      Some(new SudokuBoard(newAvailable + ((row,col) -> Set(value))))
    }
  }

  def solve(): Option[SudokuBoard] = {
    if (counter % 100000 == 0) {
      println(this)
    }

    counter = counter + 1
    if (isSolved) {
      Some(this)
    }
    else if (isUnsolvable) {
      None
    }
    else {
      for (board <- nextStates()) {
        board.solve match {
          case Some(board) => return Some(board)
          case None => ()
        }
      }
      return None
    }
  }

}

object Main extends App {
  import SudokuBoard._

  val e1 = SudokuBoard.init(List(
    (0, 0) -> 8,
    (0, 3) -> 9,
    (0, 4) -> 3,
    (0, 8) -> 2,
    (1, 2) -> 9,
    (1, 7) -> 4,
    (2, 0) -> 7,
    (2, 2) -> 2,
    (2, 3) -> 1,
    (2, 6) -> 9,
    (2, 7) -> 6 ,
    (3, 0) -> 2,
    (3, 7) -> 9,
    (4, 1) -> 6,
    (4, 7) -> 7,
    (5, 1) -> 7,
    (5, 5) -> 6,
    (5, 8) -> 5,
    (6, 1) -> 2,
    (6, 2) -> 7,
    (6, 5) -> 8,
    (6, 6) -> 4,
    (6, 8) -> 6,
    (7, 1) -> 3,
    (7, 6) -> 5,
    (8, 0) -> 5,
    (8, 4) -> 6,
    (8, 5) -> 2,
    (8,8) -> 8
    ))
  println("Solving...")
  e1.get.solve match {
    case None => println("No solution")
    case Some(x) => println(x)
  }
  println(counter)

}