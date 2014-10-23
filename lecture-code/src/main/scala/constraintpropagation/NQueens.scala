package lectures.constraintpropagation

class ChessBoard(dim: Int,
                 rows: Set[Int],
                 cols: Set[Int],
                 diagonals: Set[Int],
                 antiDiagonals: Set[Int],
                 positions: Set[(Int, Int)],
                 solution: Set[(Int, Int)]) {
  require (dim > 0)

  def posToRow(x: Int, y: Int) = x

  def posToCol(x: Int, y: Int) = y

  def posToMainDiag(x: Int, y: Int) = x + y

  // symmetric to main diagonal, actually...
  def posToAntiDiag(x: Int, y: Int) = x + y

  def canPlace(x: Int, y: Int): Boolean = {
    positions.contains((x, y)) &&
    !rows.contains(posToRow(x,y)) &&
    !cols.contains(posToCol(x,y)) &&
    !diagonals.contains(posToMainDiag(x,y)) &&
    !antiDiagonals.contains(posToAntiDiag(x,y))
  }

  def place(x: Int, y: Int): ChessBoard = {
    new ChessBoard(dim = dim,
                   rows = rows + posToRow(x,y),
                   cols = cols + posToCol(x,y),
                   diagonals = diagonals + posToMainDiag(x,y),
                   antiDiagonals = antiDiagonals + posToAntiDiag(x,y),
                   positions - ((x,y)),
                   solution + ((x,y)))
  }

  def solve(): Option[ChessBoard] = {
    if (solution.size == dim) {
      return Some(this)
    }
    else {
      for ((x, y) <- positions) {
        if (canPlace(x,y)) {
          place(x, y).solve match {
            case None => ()
            case Some(b) => return Some(b)
          }
        }
      }
    }
    return None
  }

  override def toString(): String = {
    val builder = new StringBuilder((dim + 1) * dim)
    for (y <- 0.to(dim - 1)) {
      for (x <- 0.to(dim - 1)) {
        if (solution.contains((x, y))) {
          builder += 'Q'
        }
        else {
          builder += '.'
        }
      }
      builder ++= "\n"
    }
    builder.toString
  }

}

object ChessBoard {

  def emptyBoard(dim: Int) = {
    val positions = for (i <- 0.to(dim - 1); j <- 0.to(dim - 1)) yield (i, j)
    new ChessBoard(dim,
                   rows = Set(),
                   cols = Set(),
                   diagonals = Set(),
                   antiDiagonals = Set(),
                   positions = positions.toSet,
                   solution = Set())
  }

}