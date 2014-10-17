---
layout: hw
title: Tic Tac Toe
---

For this assignment, you will write a program that given a tic-tac-toe board,
determines which player will win, or if the game will be a draw.

You're going to make significant use of Scala collections and learn the
the *Minimax algorithm*, which is a form of *backtracking search*.


## Preliminaries

You should create a series of directories that look like this:

<pre>
 ./joinlists
 |-- build.sbt
 `-- project
     `-- plugins.sbt
 `-- src
     |-- main
     |   `-- scala
     |       `-- <i>your solution goes here</i>
     `-- test
        `|-- scala
             `-- <i>your tests goes here</i>
</pre>

Your `build.sbt` file must have exactly these lines:

{% highlight scala %}
name := "tictactoe"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

The `plugins.sbt` file must have exactly this line:

{% highlight scala %}
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "2.2")
{% endhighlight %}

## Representing a Tic Tac Toe Board

We assume you know how to play Tic Tac Toe. This section talks about
the representation of tic-tac-toe boards that you will use. All
the types mentioned below are in the `cmpsci220.hw.tictactoe` package.

The sealed `Player` trait has two constructors, `O` and `X`, that represent the
two players.

We are going to treat game boards as a 3x3 matrix, where `(0,0)` is
the coordinate of the top-left corner and `(2,2)` is the coordinate of
the bottom-right corner:

<pre>
       |        |
 (0,0) | (1, 0) | (2, 0)
       |        |
-------+--------+--------
       |        |
 (0,1) | (1, 1) | (2, 1)
       |        |
-------+--------+--------
       |        |
 (0,2) | (1, 2) | (2, 2)
       |        |
</pre>

We'll represent boards as values of type `Matrix[Option[Player]]`. (`Matrix` is
defined in the `cmpsci220.hw.tictactoe` packet.)

Here are some examples:

     | |
    -+-+-
     | |
    -+-+-
     | |

{% highlight scala %}
val emptyBoard = Matrix[Option[Player]](3, None)
{% endhighlight %}

    X| |
    -+-+-
     | |
    -+-+-
     | |O

{% highlight scala %}
val ex1 = emptyBoard.set(0, 0, Some(X)).set(2, 2, Some(Y))
{% endhighlight %}

    X| |
    -+-+-
     | |
    -+-+-
    X| |O

{% highlight scala %}
val ex2 = ex1.set(0, 2, Some(X))
{% endhighlight %}

## Minimax

TODO(arjun): FILL

## Programming Task

Your task is to implement the `MinimaxLike` trait, which
has two functions: one initializes the game state and running the Minimax
algorithm. To do so, you'll have to design a class to represent the state
of the game, using the `GameLike` trait.

Use this template for your code:

{% highlight scala %}
class Game(/* add fields here */) extends GameLike[Game] {

  def isFinished(): Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

  /* Assume that isFinished} is true */
  def getWinner(): Option[Player] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def nextBoards(): List[T] = {
    throw new UnsupportedOperationException("not implemented")
  }
}

object Solution extends MinimaxLike {

  type T = Game // T is an "abstract type member" of MinimaxLike

  def createGame(board: Matrix[Option[Player]]): Game = {
    require(board.dim == 3)
    throw new UnsupportedOperationException("not implemented")
  }

  def minimax(board: Game): Option[Player] = {
    throw new UnsupportedOperationException("not implemented")
  }
}
{% endhighlight %}

I recommend proceeding in the following way:

1. Add fields to the `Game` class to represent the state of the game and
   fill in the body of the `createGame` function.

2. Implement the `isFinished` method.

3. Implement the `getWinner` method. The `Matrix` class has several methods
   that will help. There is no need for you to use direct recursion.

4. Implement the `nextBoards` method, which returns a list of
   boards that represent all the moves the next player could make.

   For example, if the current board looks like this:

       X|X|
       -+-+-
        |O|
       -+-+-
       X|O|O

    Then, it is *O*'s turn, and these are the three possible next boards:

       X|X|     X|X|O    X|X|
       -+-+-    -+-+-    -+-+-
        |O|O     |O|     O|O|
       -+-+-    -+-+-    -+-+-
       X|O|O    X|O|O    X|O|O



As you implement each successive step, you may need to revisit design
decisisions you made earlier.

## Check Your Work

Here is a trivial test suite that simply checks to see if you've defined
the `Solution` object with the right type:

{% highlight scala %}
class TrivialTestSuite extends org.scalatest.FunSuite {

  test("The solution object must be defined") {
    val obj : cmpsci220.hw.tictactoe.MinimaxLike = Solution
  }
}
{% endhighlight %}

You should place this test suite in `src/test/scala/TrivialTestSuite.scala`.
If this test suite does not run as-is, you risk getting a zero.

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.
