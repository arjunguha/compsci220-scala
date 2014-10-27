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
 ./tictactoe
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

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.5"

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
val ex1 = emptyBoard.set(0, 0, Some(X)).set(2, 2, Some(O))
{% endhighlight %}

    X| |
    -+-+-
     | |
    -+-+-
    X| |O

{% highlight scala %}
val ex2 = ex1.set(0, 2, Some(X))
{% endhighlight %}

## The Minimax Algorithm

*Minimax is an algorithm to to determine who will win (or draw)
a two-player game, if both players are playing perfectly. To do so, Minimax
searches all possible game-states that are reachable from a given inital
state. Here is an outline of a recursive implementation of Minimax:

    def minimax(game: Game): Some[Player] = {

      If it is Xs turn:

        1. If game is a winning state for X, return Some(X)
        2. If game is a draw state, return None
        3. Recursively apply minimax to all the successor states of game
           - If any recursive call produces X, return Some(X)
           - Or, if any recursive call produces None, return None
           - Or, return Some(O)

      The case for Os turn is similar.

    }

You can find several other descriptions of Minimax on the Web. But, this is
the last step of the assignment. Follow the programming directions below
and implement (and test) everything leading up to Minimax. Implementing
Minimax itself will be staightforward.

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

  def nextBoards(): List[Game] = {
    throw new UnsupportedOperationException("not implemented")
  }
}

object Solution extends MinimaxLike {

  type T = Game // T is an "abstract type member" of MinimaxLike

  def createGame(turn: Player, board: Matrix[Option[Player]]): Game = {
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
   fill in the body of the `createGame` function. `createGame` takes
   two arguments: `turn` indicates the current player's turn and
   `board` describes the state of the board. **The board may be in an
   arbitrary, even illegal state.** For example, the board may have seven Xs.
   Similarly, the `turn` could be either X or O.

2. Implement the `isFinished` method. Human players often end a game early,
   when the outcome is inevitable. However, you may find it easier to write a
   program that plays until every single square is filled. Remember that
   if you determine that the game `isFinished`, you need to be able to
   determine the winner too (using `getWinner`).


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

## Testing and Review

This assignment has an extra initial submission step. This step is due by ??.
You should submit a small set of test cases for the `isFinished` method.

For example, here is a test that checks that an empty board is not Finished.

{% highlight scala %}
  val emptyBoard = Matrix[Option[Player]](3, None)

  test("Empty board should not be isFinished()") {
    assert(!Solution.createGame(emptyBoard).isFinished())
  }
{% highlight scala %}

You will submit 5 to 10 interesting test cases for `isFinished` by the deadline.
Once you have submitted, you will be given three other students' tests to review,
which you must complete by ???. This ensures that everyone gets feedback in time
for the final assignment submission.

*Note:* You will only be reviewing test cases. This means you should *not*
submit any implementation details for review.

To ensure you only submit test cases to Captain Teach, a `submit-tests` command has been
added to the `sbt` build tool. After running it, all files within your
`src/test/scala/` directory will be packaged into a tar archive named
`test-suite.tar.gz` at the root of your project directory. This means you must
*not* have any implementation details within your test directory.

To facilitate the review process, we will be using Captain Teach. Your
`@umass.edu` e-mail address will be used to authenticate you when you
visit Captain Teach. To ensure you are logged into this account, first
visit:

https://webauth.oit.umass.edu/idp/Authn/UserPassword

Once you have authenticated your account visit:

https://www.captain-teach.org/umass-cmpsci220/assignments/

If asked, select your `@umass.edu` account as the one you would like to use
while accessing Captain Teach. From the assignments screen, select the
`Next Step` link near the `tic-tac-toe` assignment. This will bring you to
a screen where you can upload `test-suite.tar.gz`. Upload that file,
review your submission, and click `Submit` when you are ready to proceed.

Right after submitting, the page should say that the submission was successful,
and give you a "Continue" link. Click "Continue" and complete the three reviews
of other students code. You can complete the reviews in any order (and view
them all at the same time), but you must complete all three reviews.

When you receive reviews from your classmates, you will also be given the
ability to submit feedback on the review. This feedback is only for the course
staff and has no effect on your grade. We're interested in hearing if the
review was particularly helpful or unhelpful, or if you think it was wrong.
Also, if you feel the review you received is in any way inappropriate, you can
flag it to bring the problem to our attention.

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.

## Comic

<a href="http://imgs.xkcd.com/comics/tic_tac_toe.png"><img src="http://imgs.xkcd.com/comics/tic_tac_toe.png"></a>

