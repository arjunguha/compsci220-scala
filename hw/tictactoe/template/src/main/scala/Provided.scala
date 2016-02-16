// You'll need to read and understand this file, but don't change its contents.
// Do not change the contents of this file

sealed trait Player
case object X extends Player
case object O extends Player

trait GameLike[T <: GameLike[T]] {

  def isFinished(): Boolean

  /** Assume that isFinished} is true. */
  def getWinner(): Option[Player]

  def nextBoards(): List[T]
}

trait MinimaxLike {

  type T <: GameLike[T]

  def createGame(turn: Player, dim: Int, board: Map[(Int, Int), Player]): T

  def minimax(board: T): Option[Player]

}

class Matrix[A] private(val dim: Int, default: A, values: Map[(Int, Int), A]) {

  def set(x: Int, y: Int, value: A): Matrix[A] = {
    require(x >= 0 && x < dim)
    require(y >= 0 && y < dim)
    new Matrix[A](dim, default, values + ((x, y) -> value))
  }

  def rows(): List[List[A]] = {
    0.to(dim - 1).toList.map { row =>
      0.to(dim - 1).toList.map { col =>
        values.getOrElse((row, col), default)
      }
    }
  }

  def cols(): List[List[A]] = {
    0.to(dim - 1).toList.map { col =>
      0.to(dim - 1).toList.map { row =>
        values.getOrElse((row, col), default)
      }
    }
  }

  def mainDiagonal(): List[A] = {
    0.to(dim - 1).toList.map { n =>
      values.getOrElse((n, n), default)
    }
  }

  def antiDiagonal(): List[A] = {
    0.to(dim - 1).toList.map { n =>
      values.getOrElse((dim - n - 1, n), default)
    }
  }

  def toList[B](f: (Int, Int, A) => B): List[B] = {
    0.to(dim - 1).map { row =>
      0.to(dim - 1).map { col =>
        f(row, col, values.getOrElse((row, col), default))
      }
    }.flatten.toList
  }

  def toMap(): Map[(Int, Int), A] = values

  def get(x: Int, y: Int): A = {
    require(x >= 0 && x < dim)
    require(y >= 0 && y < dim)
    values.getOrElse((x, y), default)
  }

  override def toString(): String = {
    val builder = new StringBuilder((dim + 1) * dim)
    for (y <- 0.to(dim - 1)) {
      for (x <- 0.to(dim - 1)) {
        builder ++= values.getOrElse((x, y), default).toString
      }
      builder ++= "\n"
    }
    builder.toString
  }

  override def hashCode(): Int = {
    (for (i <- 0.until(dim); j <- 0.until(dim)) yield {
       this.get(i, j).hashCode
     }).sum
  }

  override def equals(other: Any): Boolean = other match {
    case other: Matrix[_] => {
      other.isInstanceOf[Matrix[_]] &&
      this.dim == other.dim &&
      (for (i <- 0.until(dim); j <- 0.until(dim)) yield {
        this.get(i, j) == other.get(i, j)
       }).forall(identity)
    }
    case _ => false
  }

}

object Matrix {

  def apply[A](dim: Int, init: A): Matrix[A] = {
    new Matrix(dim, init, Map.empty)
  }

  def fromMap[A](dim: Int, default: A, values: Map[(Int, Int), A]) = {
    for (((x, y), _) <- values) {
      require(x >= 0 && x < dim)
      require(y >= 0 && y < dim)
    }
    new Matrix(dim, default, values)
  }

}