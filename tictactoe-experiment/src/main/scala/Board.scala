sealed trait Player
case object X extends Player
case object O extends Player

// Assumes matrix is consistent: at most 1 player has one, difference
// between  Xs and Os is 0, -1, +1
class Board(turn: Player, matrix: Matrix[Option[Player]]) {

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

  def isFinalState(): Boolean = {
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
    val nextStates: List[Option[Board]] = matrix.mapWithPos { (x, y, value) => value match {
      case Some(_) => None
      case None => {
        val newMatrix = matrix.set(x, y, Some(turn))
        Some(new Board(otherPlayer(turn), newMatrix))
      }
    } }
    nextStates.filter(board => !board.isEmpty).map(board => board.get)
  }

  def minimax(): Option[Player] = {
    if (hasPlayerWon(otherPlayer(turn))) {
      Some(otherPlayer(turn))
    }
    else if (isDraw()) {
      None
    }
    else {
      val children = nextBoards.map(board => board.minimax())
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

object Minimax {



}

