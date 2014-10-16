Assignment Breakdown
--------------------

- http://magictour.free.fr/sudoku.htm

- Learning to use sequences


To get a C:


- Parse the board (manual text processing)
- the peers function (trivial and rote)
- Board.isSolved (can test on given solutions)
- Board.isValidSolution (sanity checking)
- Board.peers

To get a B:


- Board.place (eliminates things)
- Board.isUnsolvable (trivial -- if available is empty)

To get an A:

- Board.nextStates (trivial)
- Board.solve

Support code:

trait SodokuFunctions {

  // 1. Parse the board.
  def parse(str: String): SudokuBoard

  // 5. We will need this to store the set of available values
  def peers(row: Int, col: Int): Seq[(Int, Int)]

}

// Fucking self-type nonsense
trait SudokuBoardLike {

  // 2. Parse the board first and implement this function
  def valueAt(row: Int, col: Int): Option[Int]



  // 3. Easy to implement and test on any representation
  def isSolved(): Boolean

  // 4. Easy sanity check: returns true if isSolved() and the
  // solution is valid (we can parse invalid solutions)
  def isValidSolution(): Boolean

  // 6. Change representation to available: Map[(Int, Int), Set[Int]].
  //    Refactor isSolved, valueAt, and isValidSolution.
  //    While doing this refactoring, parse will break.
  override def availableValuesAt(row: Int, col: Int): Set[Int]

  // 7. place only returns a valid solution, eliminates value from
  //    the peers of (row,col) and sets the only available value for (row,col)
  //    to value.
  def place(row: Int, col: Int, value: Int): Option[this.type]

  // 8. Now that place is defined, you can re-write parse. Feel free to
  //    throw an exception if the parsed board is invalid.

  // 9. A board is unsolvable if there exists any square with 0 available
  //    values.
  def isUnsolvable(): Boolean

  // 10. Try to place one available value in every next-state.
  def nextStates(): Seq[this.type]

  // 11. Simple backtracking search. Remember to prune unsolvable branches
  def solve()

  def toString(): String = { ... }

}