package hw.tictactoe

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
