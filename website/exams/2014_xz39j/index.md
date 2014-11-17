---
layout: hw
title: 2014 Mid-term Exam
---

## Important

You have 24 hours to complete this exam. However, it is designed to require
two hours. You must not discuss this exam with anyone. You may post
questions on Piazza *privately to Instructors*. If relevant, we'll send
answers to the whole class.

## Preliminaries

Create a series of directories that look like this:

<pre>
 ./exam
 |-- build.sbt
 `-- project
     `-- plugins.sbt
 `-- src
     |-- main
     |   `-- scala
     |       |-- Solution.scala
     |       `-- Types.scala
     `-- test
         `-- scala
             `-- <i>your tests goes here</i>
</pre>

Use this for `build.sbt`:

{% highlight scala %}
scalaVersion := "2.11.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

Use this for `plugins.sbt`:

{% highlight scala %}
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "2.2")
{% endhighlight %}

Use this for `Types.scala`:

{% highlight scala %}
sealed trait BinTree[+A]
case class Node[A](lhs: BinTree[A], rhs: BinTree[A]) extends BinTree[A]
case class Leaf[A](value: A) extends BinTree[A]

trait SolutionLike {
  def fringe[A](tree: BinTree[A]): Stream[A]
  def sameFringe[A](fringe1: Stream[A], fringe2: Stream[A]): Boolean
  def filterIndex[A](pred: Int => Boolean, lst: List[A]): List[A]
  def mapmap[A,B,C](f: A => B, g: B => C, lst: List[A]): List[C]
  val notAbb: util.matching.Regex
  def isPrime(n: Int): Boolean
  val primes: Stream[Int]
}
{% endhighlight %}

Use this template for `Solution.scala`:

{% highlight scala %}
object Solution extends SolutionLike {

  def fringe[A](tree: BinTree[A]): Stream[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def sameFringe[A](fringe1: Stream[A], fringe2: Stream[A]): Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

  def filterIndex[A](pred: Int => Boolean, lst: List[A]): List[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def mapmap[A,B,C](f: A => B, g: B => C, lst: List[A]): List[C] = {
    throw new UnsupportedOperationException("not implemented")
  }

  val notAbb: util.matching.Regex = "".r

  def isPrime(n: Int): Boolean =  = {
    throw new UnsupportedOperationException("not implemented")
  }

  val primes: Stream[Int] = Stream()

}
{% endhighlight %}

## Part 1. Same Fringe

`Types.scala` defines a type `BinTree`, which represents trees with
values stored at the leaves:

{% highlight scala %}
val example1 = Node(Node(Leaf(10), Leaf(20)), Node(Leaf(30), Leaf(40)))
val example2 = Node(Leaf(10), Node(Leaf(20), Node(Leaf(30), Leaf(40))))
val example3 = Node(Leaf(1), Node(Leaf(4), Leaf(6)))
val example4 = Node(Leaf(7), Node(Leaf(7), Leaf(6)))
{% endhighlight %}

The *fringe* of a tree is the list of leaf-values in left-to-right order. For
example:

1. The fringe of `example1` is `List(10, 20, 30, 40)`
2. The fringe of `example2` is `List(10, 20, 30, 40)`
3. The fringe of `example3` is `List(1, 4, 6)`
4. The fringe of `example4` is `List(7, 7, 6)`

Notice that `example1` and `example2` have the same fringe, even though the
trees are structurally distinct. It is easy to check if two trees have the same
fringe: we simply generate the fringe and check if the two lists are equal.

However, this naive approach does a lot of unnecessary work if the two trees do
not have the same fringe. For example, consider `example3` and `example4`. By
simply examining the first elements of their fringe, it is obvious that they do
not have the same fringe. But, a naive appoach would generate the entire fringe
needlessly.

To adress this problem, we'll use streams to generate the fringe lazily.

### Programming Task

Write a function called `fringe` that produces the fringe as a `Stream[A]`.
Ensure that the elements of the stream are only generated on demand. For
example, this interaction shows that elements are only generated when queried:


    scala> val r = Solution.fringe(Node(Leaf(1), Node(Leaf(4), Leaf(6))))
    r: Stream[Int] = Stream(1, ?)
    scala> r(1); r
    res0: Stream[Int] = Stream(1, 4, ?)
    scala> r(2); r
    res1: Stream[Int] = Stream(1, 4, 6, ?)

**Hint:** Use the `#:::` operator to lazily append two streams:

    > Stream(1,2,3) #::: Stream(4,5,6)
    res2: Stream[Int] = Stream(1, ?)

Using the `fringe` function you wrote above, write a function called
`sameFringe` to determine if two streams are identical. The `sameFringe`
function should short-circuit and produce false as soon as it finds two elements
that are not identical.


## Part 2. List Processing

The `filter` function removes elements from a list that do not satisfy a given
predicate. Write a function called `filterIndex` which removes elements whose
position does not satisfy a given predicate.

For example,

1. `filterIndex(n => n < 2, List("a", "b", "c", "d")` produces `List("a", "b")`
2. `filterIndex(n => n % 2 == 0, List("a", "b", "c", "d")` produces `List("a", "c")`


The `map` function applies a function to every element in a list and returns the
list of results. It is often convenient to map several functions over a list,
e.g., `lst.map(f).map(g).map(h)`. However, this is inefficient because it
creates two intermediate lists.

Write a function called `mapmap` that applies two functions to every element
in the list, without creating an intermediate list.

The following property should hold:

    lst.map(f).map(g) == mapmap(f, g, lst)

### Part 3. Regular Expressions

Write a regular expression, `notAbb` that matches all strings of `a`s and `b`s
that do not include `abb` as a substring.

These examples should match:

- `"abaab"`
- `"bbabab"`
- `"bbb"`

These examples should not match:

- `"abb"`
- `"cab"`
- `"aababba"`

### Part 4. Primes

Write a function `isPrime(n)` that produces `true` when applied to a prime number.
You may assume that `n` is a positive number. Write a stream called `primes`
of prime numbers, starting with `2` at the head.


## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.

