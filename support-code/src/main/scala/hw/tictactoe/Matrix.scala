package cmpsci220.hw.tictactoe

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

  def mapWithPos[B](f: (Int, Int, A) => B): List[B] = {
    0.to(dim - 1).map { row =>
      0.to(dim - 1).map { col =>
        f(row, col, values.getOrElse((row, col), default))
      }
    }.flatten.toList
  }

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

}

object Matrix {

  def apply[A](dim: Int, init: A): Matrix[A] = {
    new Matrix(dim, init, Map.empty)
  }

}