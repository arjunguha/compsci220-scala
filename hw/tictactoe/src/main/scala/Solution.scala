import hw.tictactoe._

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

// Assumes matrix is consistent: at most 1 player has one, difference
// between  Xs and Os is 0, -1, +1
class Board(val turn: Player, matrix: Matrix[Option[Player]]) extends GameLike[Board] {

  def hasPlayerWon(player: Player): Boolean = {
    matrix.rows.exists { row => row.forall { cell => cell == Some(player) } } ||
    matrix.cols.exists { col => col.forall { cell => cell == Some(player) } } ||
    matrix.mainDiagonal.forall { cell => cell == Some(player) } ||
    matrix.antiDiagonal.forall { cell => cell == Some(player) }
  }

  def isDraw(): Boolean = {
    matrix.rows.forall { row =>
      row.forall { cell => !cell.isEmpty }
    }
  }

  def isFinished(): Boolean = {
    hasPlayerWon(X) || hasPlayerWon(O) || isDraw()
  }

  // Assumes isFinalState() is true.
  // None indicates draw.
  def getWinner(): Option[Player] = {
    if (hasPlayerWon(X)) {
      Some(X)
    }
    else if (hasPlayerWon(O)) {
      Some(O)
    }
    else {
      None
    }
  }

  def otherPlayer(p: Player) = p match {
    case O => X
    case X => O
  }

  // Should return empty list if isFinalState
  def nextBoards(): List[Board] = {
    val nextStates: List[Option[Board]] = matrix.toList { (x, y, value) => value match {
      case Some(_) => None
      case None => {
        val newMatrix = matrix.set(x, y, Some(turn))
        Some(new Board(otherPlayer(turn), newMatrix))
      }
    } }
    nextStates.filter(board => !board.isEmpty).map(board => board.get)
  }

}

object Solution extends MinimaxLike {

  type T = Board

  def createGame(turn: Player, dim: Int, board: Map[(Int, Int), Player]): Board = {
      new Board(turn, Matrix.fromMap(dim, None, board.mapValues(p => Some(p))))
  }

  def minimax(board: Board): Option[Player] = {
    import board._
    if (hasPlayerWon(otherPlayer(turn))) {
      Some(otherPlayer(turn))
    }
    else if (isDraw()) {
      None
    }
    else {
      val children = nextBoards.map(minimax)
      if (children.contains(Some(turn))) {
        Some(turn)
      }
      else if (children.contains(None)) {
        None
      }
      else {
        Some(otherPlayer(turn))
      }
    }
  }

}