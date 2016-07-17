import hw.tictactoe._

class Game(/* add fields here */) extends GameLike[Game] {
  def isFinished(): Boolean = ???
  /* Assume that isFinished is true */
  def getWinner(): Option[Player] = ???
  def nextBoards(): List[Game] = ???
}

object Solution extends MinimaxLike {
  type T = Game // T is an "abstract type member" of MinimaxLike
  def createGame(turn: Player, dim: Int, board: Map[(Int, Int), Player]): Game = ???
  def minimax(board: Game): Option[Player] = ???
}
