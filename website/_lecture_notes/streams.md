---
layout: lecture
title: Streams (Lazy Evaluation)
---

This lecture introduces lazy evaluation using streams.

## Streams of numbers

A stream is like a list, but its tail is only evaluated "on need".

A function to generate a list:

{% highlight scala %}
def fromTo(m: Int, n: Int): List[Int] = {
  println(s"within fromTo($m, $n)")
  if (m > n) {
    List()
  }
  else {
    m :: fromTo(m + 1, n)
  }
}

val x = fromTo(0, 100)
{% endhighlight %}

Running this code produces 100 lines of output, since the entire list is
immediately created. We can make a small change to use a stream instead:

{% highlight scala %}
def fromTo(m: Int, n: Int): Stream[Int] = {
  println(s"within fromTo($m, $n)")
  if (m > n) {
    List()
  }
  else {
    m #:: fromTo(m + 1, n)
  }
}

val x = fromTo(0, 100)
{% endhighlight %}

We now get 1 line of output each time we run `x.tail`, `x.tail.tail`.
But, running `x.tail` a second time doesn't produce any output: Scala evaluates
the tail "on need", but doesn't reevaluate the value.

## Infinite streams

We can define an infinite stream of numbers:

{% highlight scala %}
def from(n: Int): Stream[Int] = n #:: from(n + 1)

val positives = from(1)
{% endhighlight %}

This cannot be done with lists! Morever, the Scala console shows exactly what has
been evaluated:

    scala> positives.tail.tail.tail.tail
    res2: scala.collection.immutable.Stream[Int] = Stream(5, ?)
    scala> positives
    res3: Stream[Int] = Stream(1, 2, 3, 4, 5, ?)

You can work with infinite streams freely, so long as you don't try to access
all the values (your program won't halt--there are inifinitely many values).
E.g., `positives.length` does not terminate.

## Fibonnaci numbers

{% highlight scala %}
def nthFib(nPredPred: Stream[Int], nPred: Stream[Int]): Stream[Int] = {
  (nPredPred.head + nPred.head) #:: nthFib(nPredPred.tail, nPred.tail)
}

val fibs: Stream[Int] = 0 #:: 1 #:: nthFib(fibs, fibs.tail)
{% endhighlight %}


## NQueens Refactoring

This version of NQueens is much faster than what we did in class.

{% highlight scala %}
class NQueens(val dim: Int,
              val available: List[(Int, Int)],
              val solution: Set[(Int, Int)])
  with cmpsci220.hw.nqueens.NQueensToStringLike {

  def place(x: Int, y: Int): NQueens = {
    def isOccupied(p: (Int, Int)): Boolean = {
      val (x1, y1) = p
      x1 == x || y1 == y || x1 + y1 == x + y  || x1 - y1 == x - y
    }
    new NQueens(dim, available.filterNot(isOccupied), solution + ((x, y)))
  }

  def solve(): Option[NQueens] = {
    if (available.size == 0) {
      return Some(this)
    }
    else {
      for ((x, y) <- this.available) {
        this.place(x, y).solve match {
          case Some(b) => return Some(b)
          case None => ()
        }
      }
    }
    return None
  }
}

object NQueens {
  def empty(dim: Int) = {
    val available = for (x <- 0.until(dim); y <- 0.until(dim)) yield { (x, y) }
    new NQueens(dim, available.toList, Set())
  }
}
{% endhighlight %}

Let's refactor it to be more like Tic Tac Toe. i.e., lets have a `nextBoards`
function that produces a list of boards:

{% highlight scala %}
class NQueens(...) {

  ...

  def nextBoards(): List[NQueens] = {
    this.available.map(p => {
      val (x, y) = p
      this.place(x, y)
    })
  }

  def solve(): Option[NQueens] = {
    if (available.size == 0) {
      return Some(this)
    }
    else {
      for (b <- nextBoards()) {
        b.solve match {
          case Some(b) => return Some(b)
          case None => ()
        }
      }
    }
    return None
  }
{% endhighlight %}

Unfortunately, this is much less efficient, since we create every single
next-board, instead of just the first board that works.
Add a `println` within `nextBoards` to see.

We can fix this using streams:
{% highlight scala %}
class NQueens(...) {

  ...

  def nextBoards(): Stream[NQueens] = {
    this.available.toStream.map(p => {
      val (x, y) = p
      this.place(x, y)
    })
  }

  def solve(): Option[NQueens] = {
    if (available.size == 0) {
      return Some(this)
    }
    else {
      for (b <- nextBoards()) {
        b.solve match {
          case Some(b) => return Some(b)
          case None => ()
        }
      }
    }
    return None
  }
{% endhighlight %}

## Generating Regular Words

A function to generate all strings that match a regular expression:

{% highlight scala %}
import cmpsci220.hw.regex._

object Words {

  def words(re: Regex): Stream[List[Char]] = re match {
    case One => Stream(List())
    case Zero => Stream()
    case Character(ch) => Stream(List(ch))
    case Alt(lhs, rhs) => words(lhs) #::: words (rhs)
    case Seq(lhs, rhs) => {
      for (w1 <- words(lhs); w2 <- words(rhs)) yield {
        w1 ++ w2
      }
    }
    case Star(re) => words(Alt(One, Seq(re, Star(re))))
  }

}
{% endhighlight %}

