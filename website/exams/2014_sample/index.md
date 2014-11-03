---
layout: hw
title: Sample Exam (Nov 2014)
---

## Important

You have 24 hours to complete this exam. However, it is designed to require
two hours. You must not discuss this exam with anyone. You may post
questions on Piazza *privately to Instructors*. If relevent, we'll send
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
  def flipChildren[A](tree: BinTree[A]): BinTree[A]
  def removeEveryOther[A](lst: List[A]): List[A]
  def safeMap[A,B](f: A => Option[B], lst: List[A]): List[B]
  def ascendingDigits: util.matching.Regex
  def multiplesOfSeven: Stream[Int]
}
{% endhighlight %}

Use this template for `Solution.scala`:

{% highlight scala %}
object Solution extends SolutionLike {

  def flipChildren[A](tree: BinTree[A]): BinTree[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def removeEveryOther[A](lst: List[A]): List[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def safeMap[A,B](f: A => Option[B], lst: List[A]): List[B] = {
    throw new UnsupportedOperationException("not implemented")
  }

  val ascendingDigits: util.matching.Regex = """not implemented""".r

  val multiplesOfSeven: Stream[Int] = Stream()
}
{% endhighlight %}

## Exam

### Problem 1

The file Types.scala defines a type `BinTree`, which represents trees with values
stored at the leaves. Write a function that exchanges the left and right
children at all positions in a `BinTree`.

For example, given this `BinTree`:

{% highlight scala %}
val t = Node(Node(Leaf(10), Leaf(20)), Node(Leaf(30), Leaf(40)))
{% endhighlight %}

`flipChildren(t)` should produce:

{% highlight scala %}
Node(Node(Leaf(40), Leaf(30)), Node(Leaf(20), Leaf(10)))
{% endhighlight %}

### Problem 2

Write a function that removes every other element from a list, starting
with the first element.

For example:

{% highlight scala %}
removeEveryOther(List("a", "b", "c", "d")) == List("b", "d")
removeEveryOther(List()) == List()
{% endhighlight %}

### Problem 3

Suppose we want to calculate the reciprocal of every number in a list. We
could write the following:

{% highlight scala %}
def reciprocals(lst: List[Double]): List[Double] = {
  lst.map(n => 1 / n)
}
{% endhighlight %}


But, this program will throw a divide-by-zero exception if any element of lst is
0. This variant removes all the zeroes first:

{% highlight scala %}
def safeReciprocals(lst: List[Double]): List[Double] = {
  lst.filter(n => n != 0).map(n => (1 / n))
}
{% endhighlight %}

Unfortunately, this function traverses the list twice: first to filter and then
to map. Your task is to write a higher-order function called safeMap that can be
used to write safeReciprocals as follows:

{% highlight scala %}
def efficientSafeReciprocals(lst: List[Double]): List[Double] = {
  safeMap((n: Int) => if (n == 0) { None } else { Some(1 / n) }, lst)
}
{% endhighlight %}

You must take care to only traverse the list once.

{% highlight scala %}
{% endhighlight %}

### Problem 4

Write a regular expression that matches a string of digits in ascending
order.

For example, these strings are in ascending order:

- `"0123456789"`
- `"456"`
- `"556779"`

However, these strings are not in ascending order:

- `"10"`
- `"1231"`

### Problem 5

**We will cover streams during the week of Nov 9.**

Write a stream of the multiples of 7, starting with 7.

- `multiplesOfSeven.head == 7`
- `multiplesOfSeven.tail.head == 14`
- `multiplesOfSeven.tail.tail.head == 21`

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.

