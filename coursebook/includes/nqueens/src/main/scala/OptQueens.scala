class OptQueens(val dim: Int, val solution: Set[(Int, Int)],
                available: List[(Int, Int)]) extends ChessBoardLike {

  def place(x: Int, y: Int): OptQueens = {
    new OptQueens(dim,
      solution + ((x, y)),
      available.filter(p => {
        val (x1, y1) = p
        !(x == x1 || y == y1 || x + y == x1 + y1 || x - y == x1 - y1)
    }))
  }

  def solveRec(coords: List[(Int, Int)]): Option[OptQueens] = coords match {
    case (x, y) :: rest => this.place(x, y).solve.orElse(solveRec(rest))
    case _ :: rest => solveRec(rest)
    case Nil => None
  }

  def solve(): Option[OptQueens] = (solution.size == dim) match {
    case true => Some(this)
    case false => solveRec(available.toList)
  }
}

object OptQueens {
  def empty(dim: Int) = {
    val available = 0.until(dim).flatMap(x => 0.until(dim).map(y => (x, y))).toList
    new OptQueens(dim, Set(), util.Random.shuffle(available))
  }
}