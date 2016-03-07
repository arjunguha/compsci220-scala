class NaiveQueens(val dim: Int, val solution: Set[(Int,Int)]) extends ChessBoardLike {

  def canPlace(x: Int, y: Int): Boolean = solution.forall {
    case (x1, y1) => x != x1 && y != y1 && x + y != x1 + y1 && x - y != x1 - y1
  }

  def solveRec(coords: List[(Int, Int)]): Option[NaiveQueens] = coords match {
    case (x, y) :: rest if (canPlace(x, y)) => {
      val optSol = (new NaiveQueens(dim, solution + ((x, y)))).solve()
      optSol.orElse(solveRec(rest))
    }
    case _ :: rest => solveRec(rest)
    case Nil => None
  }

  def solve(): Option[NaiveQueens] = (solution.size == dim) match {
    case true => Some(this)
    case false => solveRec(0.until(dim).flatMap(x => 0.until(dim).map(y => (x, y))).toList)
  }
}