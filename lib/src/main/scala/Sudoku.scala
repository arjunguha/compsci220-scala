package hw.sudoku

trait SudokuLike {
  type T <: BoardLike[T]
  def parse(board: String): T
  def peers(row: Int, col: Int): Iterable[(Int, Int)]
}

abstract class BoardLike[T <: BoardLike[T]] {

  def valueAt(row: Int, col: Int): Option[Int]
  def isSolved(): Boolean
  def availableValuesAt(row: Int, col: Int): Iterable[Int]
  def place(row: Int, col: Int, value: Int): T
  def isUnsolvable(): Boolean
  def nextStates(): Iterable[T]
  def solve(): Option[T]

  private def toStringUsingValueAt(): String = {
    val b = new StringBuilder()
    for (r <- 0.to(8)) {
      for (c <- 0.to(8)) {
        valueAt(r, c) match {
          case Some(x) => b.append(x.toString)
          case None => b.append(" ")
        }
      }
      b += '\n'
    }
    b.toString
  }

  private def toStringUsingAvailableValuesAt(): String = {
    val b = new StringBuilder()
    for (r <- 0.to(8)) {
      for (c <- 0.to(8)) {
        b.append(availableValuesAt(r, c).mkString.padTo(10, " ").mkString)
      }
      b += '\n'
    }
    b.toString
  }

  /**
   * Prints the state of the board.
   *
   * If {@code availableValuesAt} has been implemented, prints the available
   * values at each cell. Otherwise, prints the value at each cell by invoking
   *  {@code valueAt}.
   */
  override def toString(): String = {
    try {
      toStringUsingAvailableValuesAt()
    }
    catch {
      case _: Throwable => {
        try {
          toStringUsingValueAt()
        }
        catch {
          case _: Throwable => {
            """BoardLike: cannot display the board. valueAt threw an exception"""
          }
        }
      }
    }
  }
}
