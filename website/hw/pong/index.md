---
layout: hw
title: Pong
---

## Introduction

According to [Wikipedia], Pong was one of the <span style="border-bottom: 1px
dotted black" title="citation needed">first video games to go mainstream</span>.
You should first [play the game] to understand how Pong works.


In this assignment, you will write a program that allows two players to play
Pong. Both players will control their paddles using the keyboard:

- **a** Player 1 paddle moves up
- **z** Player 1 paddle moves down
- **k** Player 2 paddle moves up
- **m** Player 2 paddle moves down

To encourage cooperation, your game won't keep score. But, you will implement
Pong physics (i.e., basic collision detection).

## Part 0: Preliminaries

We are using the course graphics API. In addition, the `cmpsci22.hw.pong`
package defines the `Vector2D` type and some geometry functions that will
help with the last part of this assignment.

Save your work in a file called `pong.scala`. It should begin with these
imports:

{% highlight scala %}
import cmpsci220._
import cmpsci220.graphics._
import cmpsci220.hw.pong._
{% endhighlight %}


## Part 1: Paddle Motion

In this part, you'll ignore the ball and focus on the paddles. We'll guide
you through implementing the following behavior:

- You'll use types that we have provided to represent the board, the height
  and position of each paddle, and the velocity of each paddle.
- You will write functions to respond to keys being pressed and released. When
  players press the right keys, you will update their paddle velocity, but
  *you will not move their paddles.*
- Instead, you'll write a function that is applied on each clock tick to move
  both paddles based on their velocity.
- Finally, you'll write a function to draw the board and paddles. This will
  be quite straightforward, since the paddles are simple straight lines.

Use these types to represent the game state. For now, we are ignoring the ball:

{% highlight scala %}

// Assume that width > 0 and height > 0
case class Table(width: Int, height: Int)

// (x,y) is the bottom coordinate and (x, y + height) is the top coordinate
case class Paddle(pos: Vector2D, height: Double)

case class Game(table: Table,
                paddle1: Paddle,
                paddle2: Paddle,
                velocity1: Vector2D,
                velocity2: Vector2D)
{% endhighlight %}

Define a value `initGame` that represents the start state:

{% highlight scala %}
val initGame : Game
{% endhighlight %}

Pick a size for the table and positions for the paddles that make sense
for your computer screen. However, both initial velocities should be zeroed.

A paddle must fit within the table. However, since the table's dimensions
are not fixed, we can only determine if a paddle is valid relative to a
given table. Write the following function to do so:

{% highlight scala %}
def isValidPaddle(paddle: Paddle, table: Table): Boolean
{% endhighlight %}

Write a function to move a paddle:

{% highlight scala %}
def movePaddle(paddle: Paddle, table: Table, velocity: Vector2D): Option[Paddle]
{% endhighlight %}

`movePaddle` should assume that `isValidPaddle(paddle, table)` holds. The
function should produce a *valid* paddle that is offset by `(velocity.dx,
velocity.dy)` or `None`.

*Hint*: See the API documentation for `Vector2D`. It has some useful methods.

Since players will control their paddles with the keyboard, we have to respond
to keys being pressed and released as follows:

- When Player 1 presses **a**, update their paddle velocity to move the their
  paddle up
- When Player 1 presses **z**, update their paddle velocity to move the their
  paddle down
- When Player 1 releases either **a** or **z** set their paddle velocity to zero
- When Player 2 presses **k**, update their paddle velocity to move the their
  paddle up
- When Player 2 presses **m**, update their paddle velocity to move the their
  paddle down
- When Player 2 releases either **a** or **z** set their paddle velocity to zero

**Note:** when players press keys, you need to update their paddle velocity, but
*do not change the paddle position*. You will write that separately.

First, write a function that responds to key-presses:

{% highlight scala %}
def keyPressed(key: String, game: Game): Game = key match {
  case "a" => ... // change Player 1 paddle velocity to move up
  case "z" => ... // change Player 1 paddle velocity to move down
  case "k" => ... // change Player 2 paddle velocity to move up
  case "m" => ... // change Player 2 paddle velocity to move up
  case _ => game  // ignore the key
}
{% endhighlight %}

You can use the same template to respond to key-releases:

{% highlight scala %}
def keyReleased(key: String, game: Game): Game
{% endhighlight %}

Now that you have functions to update paddle velocity, you need to write
a function to update paddle positions, based on their velocity:

{% highlight scala %}
// Do not update velocities
def moveBothPaddles(game: Game): Game
{% endhighlight %}

You should use the `movePaddle` function that you defined above.

Write a function to draw the table and both paddles:

{% highlight scala %}
// No need to write tests cases for this function
def drawGame(game: Game): Image
{% endhighlight %}

Finally, you can put all these pieces together with `animate`:

{% highlight scala %}
animate(init = initGame,
        draw = drawGame,
        width = initGame.table.width,
        height = initGame.table.height,
        tick = moveBothPaddles,
        keyPressed = keyPressed,
        keyReleased = keyReleased)
{% endhighlight %}

If you're written and tested all the functions correctly, you should be able to
control both paddles with the keyboard. Of course, you're missing the ball,
which you'll implement next.

**Check Your Work**: From the command-line, run the command:

    check220 check pong step1

## Part 2: Refactoring to add a Ball

In this part, you'll add the ball to the game-state. You'll focus on
editing the code your wrote in Part 1 and drawing the ball. But, you won't
implement ball motion or collision detection yet.

Use the following datatype to represent the size, position and velocity of the
ball:

{% highlight scala %}
case class Ball(radius: Double, position: Vector2D, velocity: Vector2D)
{% endhighlight %}

Change the `Game` type to include a ball:

{% highlight scala %}
case class Game(table: Table,
                paddle1: Paddle,
                paddle2: Paddle,
                velocity1: Vector2D,
                velocity2: Vector2D,
                ball: Ball)
{% endhighlight %}

You have to modify the existing code. You should update `drawGame` to
draw the ball, but do not make the ball move yet. Simply ensure that all
existing tests pass.

**Check Your Work**: From the command-line, run the command:

    check220 check pong step2

## Part 3: Ball Motion

The challenge with moving the ball is to implement collision detection
correctly. First, write functions to determine if the ball has collided with
the table edge or a paddle:

{% highlight scala %}
def hasHitTable(ball: Ball, table: Table): Boolean

def hasHitPaddle(ball: Ball, paddle: Paddle): Boolean
{% endhighlight %}

In principle, both functions need to calculate the distance between
a point (the center of the ball) and a line segment (a paddle or an edge of
the table). You have to account for the radius of the ball too. `Vector2D`
has some methods that will help you. If you want to do the math yourself,
you can use the [law of cosines].

Write the following function:

{% highlight scala %}
// Assumes that ball is not currently in a collision
def moveBall(game: Game): Game
{% endhighlight %}

This function should move the ball along its current trajectory if no collision
will occur. If a collision would occur, it should leave the position of the
ball unchanged, but update the velocity to simulate a bounce.

Finally, update the call to `animate` to move both the ball and the paddles:

{% highlight scala %}
animate(init = initGame,
        draw = drawGame,
        width = initGame.table.width,
        height = initGame.table.height,
        tick = (game: Game) => { moveBall(moveBothPaddles(game)) },
        keyPressed = keyPressed,
        keyReleased = keyReleased)
{% endhighlight %}

At this point, the game should be playable.

**Check Your Work**: From the command-line, run the command:

    check220 check pong final

Hand In
-------

From the command-line, run the command:

    check220 tar pong final

This command will create the file `submission-pong-final.tgz`. Submit this
file using Moodle.



[Wikipedia]: http://en.wikipedia.org/wiki/Pong
[play the game]: http://www.ponggame.org/
[law of cosines]: http://en.wikipedia.org/wiki/Law_of_cosines