---
layout: hw
title: Pong
---

Introduction
------------

For this assignment, you will write a version of Pong. According to [Wikipedia],
it was one of the <span title="citation needed">first video games to go
mainstream</span>. You should first [play the game] to understand how it works.
The assignment will walk you through writing a simple version of Pong.

Data Types
----------

Direct students to creating these types:

- Table(width : Int, height : Int)

- Ball(x : Double, y : Double, xv : Double, yv : Double)

- Paddle(x : Double, y : Double, yv : Double)

- Game(table : Table, ball : Ball, player1 : Paddle, player2 : Paddle)

Constants
---------

Board dimensions, paddle and ball dimensions, velocities

Initial Conditions
------------------

Describe these variables in words

val initTable

val initBall

val initPlayer1

val initPlayer2

val initGame = Game(initTable, initBall, initPlayer1, initPlayer2)

Moving Paddles
--------------

These functions are totally straightforward:

def driftNorth(p : Paddle) : Paddle

def driftSouth(p : Paddle) : Paddle

def stopDrifting(p : Paddle) : Paddle

This function requires using the image library and constants.

def drawPaddle(p : Paddle) : Image

movePaddle is more sophisticated:

def movePaddle(p : Paddle) : Paddle

Students have to implement collision detection with the board edge (using the
constants above). They have to properly account for the height of the paddle
too. Direct them to write a function called isEdgeCollision, to detect
collisions and then either call stopDrifting or update the position of the
paddle.

For testing, test identities such as:

  movePaddle(driftSouth(movePaddle(driftNorth(p)))) == p

Also direct the students to test boundary conditions.

Describe how keyPressed and keyReleased work. Provide template for
keyPressed

def keyPressed(key : String, game : Game) : Game = key match {
  case "a" =>
  case "z" =>
  case "k" =>
  case "m" =>
  case _ => game // ignoring key
}

def keyReleased(key : String, game : Game) : Game

Finally, wiring it all up:

def tick(game : Game) : Game =
  Game(movePaddle(game.player1), movePaddle(game.player2), game.ball)

animate(initGame, tick = tick, ....)

Moving the Ball
---------------

The main problem is collision detection.

- Colliding with the top, bottom is easy
- Colliding with the left or right edge changes score
- Colliding with a paddle is also easy, but the paddle changes position

// Use auxiliary functions
def ballHitEdge(ball : Ball) : Ball

def ballHitPaddle(ball : Ball, paddle : Paddle) : Ball

def ballMotion(ball : Ball) : Ball

def moveBall(ball : Ball, player1 : Paddle, player2 : Paddle) : Ball =
  ballMotion(ballHitEdge(ball, ballHitPaddle(ballHitPaddle(ball, player1), player2)))

def drawBall(ball : Ball) : Image

Wiring it all up:

def tick(game : Game) : Game =
  Game(movePaddle(game.player1), movePaddle(game.player2),
       moveBall(game.ball, game.player1, game.player2))

Extra Credit: Keep Score
------------------------

Keep track of the score.


[Wikipedia]: http://en.wikipedia.org/wiki/Pong
[play the game]: http://www.ponggame.org/