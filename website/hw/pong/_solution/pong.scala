import cmpsci220._
import cmpsci220.graphics._
import cmpsci220.hw.pong._


// Assume that width > 0 and height > 0
case class Table(width: Int, height: Int)

// (x,y) is the bottom coordinate and (x, y + height) is the top coordinate
case class Paddle(pos: Vector2D, height: Double)

case class Ball(radius: Double, position: Vector2D, velocity: Vector2D)

case class Game(table: Table,
                paddle1: Paddle,
                paddle2: Paddle,
                velocity1: Vector2D,
                velocity2: Vector2D,
                ball: Ball)


val initGame = Game(Table(800, 600),
                    Paddle(Vector2D(100, 200), 50),
                    Paddle(Vector2D(700, 300), 50),
                    Vector2D(0, 0),
                    Vector2D(0, 0),
                    Ball(10, Vector2D(400, 300), Vector2D(10, 5)))

def isValidPaddle(p: Paddle, t: Table): Boolean = {
  p.pos.y > 0 && p.pos.y + p.height < t.height && p.pos.x > 0 && p.pos.x < t.width
}

def movePaddle(paddle: Paddle, table: Table, velocity: Vector2D): Option[Paddle] = paddle match {
  case Paddle(pos, height) => {
    val moved = Paddle(pos + velocity, height)
    if (isValidPaddle(moved, table)) {
      Some(moved)
    }
    else {
      None
    }
  }
}

def keyPressed(key: String, game: Game): Game = key match {
  case "a" => game.copy(velocity1 = Vector2D(0, 10))
  case "z" => game.copy(velocity1 = Vector2D(0, -10))
  case "k" => game.copy(velocity2 = Vector2D(0, 10))
  case "m" => game.copy(velocity2 = Vector2D(0, -10))
  case _ => game
}

def keyReleased(key: String, game: Game): Game = key match {
  case "a" => game.copy(velocity1 = Vector2D(0, 0))
  case "z" => game.copy(velocity1 = Vector2D(0, 0))
  case "k" => game.copy(velocity2 = Vector2D(0, 0))
  case "m" => game.copy(velocity2 = Vector2D(0, 0))
  case _ => game
}

def movePaddleOrNot(paddle: Paddle, table: Table, velocity: Vector2D): Paddle = {
  movePaddle(paddle, table, velocity) match {
    case Some(p) => p
    case None => paddle
  }
}

def moveBothPaddles(game: Game): Game = game match {
  case Game(t, p1, p2, v1, v2, b) =>
    Game(t,
         movePaddleOrNot(p1, t, v1),
         movePaddleOrNot(p2, t, v2),
         v1,
         v2,
         b)
}

def drawPaddle(p: Paddle): Image = move(line(0, p.height / 2), p.pos.x, p.pos.y)

def drawBall(b: Ball): Image =
move(solidOval(b.radius * 2, b.radius * 2), b.position.x, b.position.y)

def drawGame(game: Game): Image =
  overlay(rect(game.table.width, game.table.height),
          overlay(drawBall(game.ball),
          overlay(drawPaddle(game.paddle1), drawPaddle(game.paddle2))))

def hasHitTable(ball: Ball, table: Table): Boolean = {
  val r = ball.radius
  val p = ball.position
  val p1 = Vector2D(0, 0)
  val p2 = Vector2D(table.width, 0)
  val p3 = Vector2D(table.width, table.height)
  val p4 = Vector2D(0, table.height)
  val b = (p.distanceToLineSegment(p1, p2) <= r ||
   p.distanceToLineSegment(p2, p3) <= r ||
   p.distanceToLineSegment(p3, p4) <= r ||
   p.distanceToLineSegment(p1, p4) <= r)
  if (b) {
    println("Hit table")
  }
  b
}


def hasHitPaddle(ball: Ball, paddle: Paddle): Boolean = {
  val b = ball.position.distanceToLineSegment(paddle.pos, paddle.pos + Vector2D(0, paddle.height)) < ball.radius
  if (b) {
    println("Hit paddle")
  }
  b

}

def moveBall(g: Game): Game = {
  val moved = g.ball.copy(position = g.ball.position + g.ball.velocity)
  if (hasHitTable(moved, g.table) ||
      hasHitPaddle(moved, g.paddle1) ||
      hasHitPaddle(moved, g.paddle2)) {
    g.copy(ball = g.ball.copy(velocity = -g.ball.velocity))
  }
  else {
    g.copy(ball = moved)
  }
}


animate(init = initGame,
        draw = drawGame,
        width = initGame.table.width,
        height = initGame.table.height,
        tick = (game: Game) => { moveBall(moveBothPaddles(game)) },
        keyPressed = keyPressed,
        keyReleased = keyReleased)

// animate(init = initGame,
//         draw = drawGame,
//         width = initGame.table.width,
//         height = initGame.table.height,
//         tick = moveBothPaddles,
//         keyPressed = keyPressed,
//         keyReleased = keyReleased)

