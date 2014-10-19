package cmpsci220.hw.sudoku

trait SudokuLike {

  type T <: BoardLike[T]

  def parse(board: String): T

  def peers(row: Int, col: Int): Seq[(Int, Int)]

}

abstract class BoardLike[T <: BoardLike[T]] {

  def valueAt(row: Int, col: Int): Option[Int]

  def isSolved(): Boolean

  def availableValuesAt(row: Int, col: Int): Set[Int]

  def place(row: Int, col: Int, value: Int): T

  def isUnsolvable(): Boolean

  def nextStates(): Seq[T]

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

// trait BoardLike[T <: BoardLike[T]] {



//   // 6. Change representation to available: Map[(Int, Int), Set[Int]].
//   //    Refactor isSolved, valueAt, and isValidSolution.
//   //    While doing this refactoring, parse will break.
//   override def availableValuesAt(row: Int, col: Int): Set[Int]

//   // 7. place only returns a valid solution, eliminates value from
//   //    the peers of (row,col) and sets the only available value for (row,col)
//   //    to value.
//   def place(row: Int, col: Int, value: Int): Option[T]

//   // 8. Now that place is defined, you can re-write parse. Feel free to
//   //    throw an exception if the parsed board is invalid.

//   // 9. A board is unsolvable if there exists any square with 0 available
//   //    values.
//   def isUnsolvable(): Boolean

//   // 10. Try to place one available value in every next-state.
//   def nextStates(): Seq[T]

//   // 11. Simple backtracking search. Remember to prune unsolvable branches
//   def solve()

