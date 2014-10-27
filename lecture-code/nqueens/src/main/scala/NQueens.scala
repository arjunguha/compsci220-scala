object Main extends App {

  def time[A](body: => A): A = {
    val t0 = System.currentTimeMillis
    val r: A = body
    val t = System.currentTimeMillis
    println(s"Time: ${t - t0}ms")
    r
  }

  trait ChessBoardLike {
    val dim: Int
    val solution: Set[(Int, Int)]

    require (dim > 0)

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

  // Allows illegal states
  class NaiveQueens(val dim: Int, val solution: Set[(Int, Int)])
    extends ChessBoardLike {

     def place(x: Int, y: Int): NaiveQueens = {
       require(!solution.contains((x, y)))
       require(x >= 0 && x < dim)
       require(y >= 0 && y < dim)
       new NaiveQueens(dim, solution + ((x, y)))
     }

  }

  object NaiveQueens {

    def empty(dim: Int) = new NaiveQueens(8, Set())

  }


  println(NaiveQueens.empty(8).place(0, 0).place(0, 1).place(7, 7))

  def posToRow(x: Int, y: Int) = y

  def posToCol(x: Int, y: Int) = x

  def posToMainDiag(x: Int, y: Int) = x + y

  def posToAntiDiag(x: Int, y: Int) = x - y

  class CheckedQueens(val dim: Int, val solution: Set[(Int,Int)])
    extends ChessBoardLike {

    // We have to copy code! Can't inherit
    def place(x: Int, y: Int): CheckedQueens = {
      require(!solution.contains((x, y)))
      require(x >= 0 && x < dim)
      require(y >= 0 && y < dim)
      new CheckedQueens(dim, solution + ((x, y)))
    }

    def canPlace(x: Int, y: Int): Boolean = {
      solution.forall { p =>
        val (x1, y1) = p
        posToRow(x, y) != posToRow(x1, y1) &&
        posToCol(x, y) != posToCol(x1, y1) &&
        posToMainDiag(x, y) != posToMainDiag(x1, y1) &&
        posToAntiDiag(x, y) != posToAntiDiag(x1, y1)
      }
    }

    def solve(): Option[CheckedQueens] = {
      if (solution.size == dim) {
        return Some(this)
      }
      else {
        for (i <- 0.until(dim); j <- 0.until(dim)) {
          if (canPlace(i, j)) {
            place(i, j).solve match {
              case None => ()
              case Some(solution) => return Some(solution)
            }
          }
        }
        return None
      }
    }
  }

  val empty5 = new CheckedQueens(5, Set())
  println(empty5.solve.get)

  // takes a second on Core i5 Haswell 1.3 GHz. 12 takes much longer.
  val empty11 = new CheckedQueens(11, Set())
  println(time(empty11.solve.get))

  println(time(new CheckedQueens(12, Set()).solve.get))

  class OptQueens(val dim: Int,
                  val solution: Set[(Int, Int)],
                  rows: Set[Int],
                  cols: Set[Int],
                  diagonals: Set[Int],
                  antiDiagonals: Set[Int],
                  positions: Set[(Int, Int)]) extends ChessBoardLike {

    def canPlace(x: Int, y: Int): Boolean = {
      positions.contains((x, y)) &&
      !rows.contains(posToRow(x,y)) &&
      !cols.contains(posToCol(x,y)) &&
      !diagonals.contains(posToMainDiag(x,y)) &&
      !antiDiagonals.contains(posToAntiDiag(x,y))
    }

    def place(x: Int, y: Int): OptQueens = {
      new OptQueens(dim = dim,
                    rows = rows + posToRow(x,y),
                    cols = cols + posToCol(x,y),
                    diagonals = diagonals + posToMainDiag(x,y),
                    antiDiagonals = antiDiagonals + posToAntiDiag(x,y),
                    positions = positions - ((x,y)),
                    solution = solution + ((x,y)))
    }

    def solve(): Option[OptQueens] = {
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

  }

  object OptQueens {

    def empty(dim: Int) = {
      val positions = for (i <- 0.to(dim - 1); j <- 0.to(dim - 1)) yield (i, j)
      new OptQueens(dim = dim,
                    rows = Set(),
                    cols = Set(),
                    diagonals = Set(),
                    antiDiagonals = Set(),
                    positions = positions.toSet,
                    solution = Set())
    }

  }

  println(time(OptQueens.empty(11).solve.get))
  println(time(OptQueens.empty(12).solve.get))

  println(time(OptQueens.empty(13).solve.get))




}