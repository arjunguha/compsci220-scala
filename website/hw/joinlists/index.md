---
layout: hw
title: Join Lists
---

The type `List[A]` only allows sequential access to elements. For example, you
can't get to the third element until you've been through the first two.
Instead, if you could recur on sub-lists that were significantly smaller
and run each recursive call on on a different processor core, you could
obtain significant speedup. This requires a different data structure,
which is what we'll explore in this assignment. Remarkably, this
new data structure, will support the same operations as a list.

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
name := "joinlists"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

The `plugins.sbt` file must have exactly this line:

{% highlight scala %}
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "2.2")
{% endhighlight %}

When we grade your work, we will delete your `build.sbt` file and use the
one above. So, if you change it, you risk getting a zero.

## Introduction to Join Lists

You'll be working with the `JoinList[A]` type, which is part
of the `cmpsci220.hw.joinlists` package. It has three constructors:

- `Empty()` represents an empty list
- `Singleton(x)` represents a list with one element `x`
- `Join(lst1, lst2, length)` represents `lst1` appended to `lst2`. The
  `length` field is the total number of elements in the list.

It should be clear that it is very cheap to append two join lists: you simply
use the `Join` constructor. It is also cheap to calculate the length of a
join list, since it is stored at each non-trivial node of the tree.

The package includes two simple, helper functions:

- `length[A](lst: JoinList[A]): Int` produces the length of a list. It assumes
  that the `length` field is accurate.

- `join[A](lst1: JoinList[A], lst2: JoinList[A]): JoinList[A]` is what's
  known as a *smart constructor*. If either `lst1` or `lst2` is `Empty()`,
  it just produces the other list. More significantly, instead of naively
  joining the two lists, it produces a size-balanced join list. If you're
  interested, go read the code. It is quite cool.

Finally, since join lists representat for lists, the package has two functions
to convert between join lists and lists:

- `toList[A](lst: JoinList[A]): List[A]` converts a join list into a Scala
  list. i.e., not the `cmpsci220.List` type. It is a simple function that
  turns `Join(lst1, lst2)`s into list-appends (the `++` operator). This
  operation can be very expensive, but is useful for testing.

- `fromList[A](lst: List[A]): JoinList[A]` converts a Scala list into a join
  list by repeatedly applying the `join` smart constructor.

**Both `toList` and `joinList` are provided for testing only. You must
not use them in your solution. If you do, you risk getting a zero.**

## Programming Task

In the file `src/main/scala/Solution.scala`, you must define an
object called `Solution` that implements the `JoinListFunctions` trait.
You can use the this template:

{% highlight scala %}
// src/main/scala/Solution.scala
import cmpsci220.hw.joinlists._

object Solution extends JoinListFunctions {

  def max[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def map[A,B](f: A => B, lst: JoinList[A]): JoinList[B] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def filter[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def first[A](lst: JoinList[A]): Option[A] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def rest[A](lst: JoinList[A]): Option[JoinList[A]] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def nth[A](lst: JoinList[A], n: Int): Option[A] =  {
    throw new UnsupportedOperationException("not implemented")
  }

}
{% endhighlight %}

Here are the specifications of these functions:

- `max(lst, compare)` produces the maximum value in `lst`. The second argument
  is a comparator. If `compare(x, y) == true`, then `x` is greater than `y`. If
  `lst` is empty, `max` produces `None`.

- `map(f, lst)` produces a new `lst` which has the same structure as `lst`,
  but with `f` applied to every element.

- `filter(pred, lst)` removes all the elements of `lst` that do not satisfy
  the given predicate.

- `first(lst)` and `rest(lst)` produce the head and tail, respectively,
   of `lst` if `lst is non-empty.

- `nth(lst, i)` produces the `n`th element of the list.

## Check your work

Here is a trivial test suite that simply checks to see if you've defined
the `Solution` object with the right type:

{% highlight scala %}
class TrivialTestSuite extends org.scalatest.FunSuite {

  test("The solution object must be defined") {
    val obj : cmpsci220.hw.joinlists.JoinListFunctions = Solution
  }
}
{% endhighlight %}

You should place this test suite in `src/test/scala/TrivialTestSuite.scala`.
If this test suite does not run as-is, you risk getting a zero.

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.
