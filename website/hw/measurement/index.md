---
layout: hw
title: Measurement
---

## Step -1

Run this command from the terminal:

    echo 'export SBT_OPTS="-Xmx512m -Xss100m"' >> ~/.profile

This increases the stack size limit for SBT to 100MB and sets the memory limit
to 512MB.

<img src="http://imgs.xkcd.com/comics/log_scale.png">

For this assignment, you will measure the time it takes to execute typical
operations on several represetations of integer-sets: ordered association lists,
(unbalanced) binary search trees, and AVL trees. We provide the data structures,
but you have to develop the benchmarking framework.

## Optional Reading

You already know how to implement sets using ordered lists and binary search
trees. However, you probably are not familiar with AVL trees. We
strongly recommend reading the [Sets Appeal] chapter from
[Programming and Programming Languages]. It briefly describes how to represent
sets using ordered-lists and binary search trees, but spends most of its time
discussing AVL trees.

## Preliminaries

With this assignment, we will cease using the `scala220` program and start
using professional build tools: sbt and ScalaTest. These are covered in the
reading for the week.

As discussed in the sbt tutorial, you should create a series of directories
that look like this:

<pre>
 ./measurement
 |-- build.sbt
 `-- src
     |-- main
     |   `-- scala
     |       `-- <i>implementation goes here</i>
     `-- test
        `|-- scala
             `-- <i>test cases go here</i>
</pre>

Your `build.sbt` file must have exactly these lines:

{% highlight scala %}
name := "measurement"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

When we grade your work, we will delete your `build.sbt` file and use the
one above. So, if you change it, you risk getting a zero.

You should use [this template](Solution.scala) for your solution. You have to
fill in several functions. The rest of this page will walk you through them.

## 1. Timing Functions

Write a higher-order function `time(f, a)` that calculates the time
needed to execute `f(a)` in *milliseconds*.

{% highlight scala %}
def time[A, B](f : A => B, a : A) : Long
{% endhighlight %}

Use the the builtin function `System.currentTimeMillis()`, which returns the
current time in milliseconds (since [January 1, 1970]).


Write a higher-order function `averageTime(n, f, a)` that calculates the time
needed to execute `f(a)` in milliseconds, averaged over `n` executions.

{% highlight scala %}
def averageTime[A, B](n: Int, f: A => B, a: A) : Double
{% endhighlight %}


An easy way to test these functions is to use `Thread.sleep(n)`,
which pauses execution for `n` milliseconds. Therefore,
`time((n:Int) => Thread.sleep(n), n)` should be approximately
`n`.

## 2. Test Data Generation

You'll be benchmarking these data structures on two types of inputs: ordered
keys and randomly generated keys.

Write a recursive function `revOrder(n)` that produces the list of numbers
from `n` down to `1`:

{% highlight scala %}
// Assume that n >= 0
def revOrder(n : Int) : List[Int]

test("revOrder(5)") {
  assert(revOrder(5) == List(5, 4, 3, 2, 1))
}

test("revOrder(0)") {
  assert(revOrder(0) == Empty())
}
{% endhighlight %}

Write a recursive function `randomInts(n)` that produces a list of `n` randomly
chosen integers.

{% highlight scala %}
def randomInts(n : Int) : List[Int]
{% endhighlight %}

You will need to use the builtin function `util.Random.nextInt()`.

## 3. Test Cases

The `cmpsci220.hw.measurement` package defines several functions to insert
and lookup elements into ordered lists, binary search trees, and AVL trees.
But, to benchmark these data structures, you need to write functions to
insert/lookup several elements.

You need to write the following *six* functions (don't start yet--
finish reading this section).

{% highlight scala %}
// Map all keys to the provided value
def insertAllOrdList(values: List[Int]): OrdList
def insertAllBST(values: List[Int]): BST
def insertAllAVL(values: List[Int]): AVL

// Returns true only if all keys are mapped to the provided value
def isMemberAllOrdList(values: List[Int], lst: OrdList) : Boolean
def isMemberAllBST(values: List[Int], bst: BST) : Boolean
def isMemberAllAVL(values: List[Int], bst: AVL) : Boolean
{% endhighlight %}

Notice that all the `insert` functions follow the same pattern and so
do all the `isMemberAll` functions.

You actually only need to write the following higher-order functions
and use them to define the functions above:

{% highlight scala %}
def insertAll[T](empty : T, insert : (Int, T) => T, values: List[Int]): T

def isMemberAll[T](isMember : (Int, T) => Boolean, values: List[Int], container: T): Boolean
{% endhighlight %}

If you're not confident writing these functions immediately, you should first
write some of the simpler functions above until the pattern becomes clear.

## 4. Benchmarking

Once you have `insertAll` and `isMemberAll` defined, you can benchmark any of
the three data structures by creating a growing sequence of ordered/random keys,
picking a number of trials, and appling `insertAll` or `isMemberAll` to the list
of integers:

For example, here is some code to benchmark the time needed to insert consecutive
values into the three data structures:

{% highlight scala %}
/**
 * Calculates the average time needed to insert values into the set
 *
 * @param insertAll a function that inserts a list of values into an empty set
 * @param trials the number of trials to run
 * @param values the list of values to insert
 * @returns A pair (n, t), where n is the number of values and t is the
 *          average time needed to insert all values, divided by the
 *          number of values
 */
def timeInsertAll[A](insertAll: List[Int] => A, trials: Int)(values: List[Int]): (Double, Double) = {
  val n = length(values).toDouble
  val t = averageTime(trials, insertAll, values)
  (n, t / n)
}

test("timing insertAllAVL on random input") {
  // You may need to tweak this data to suit your computer
  val data = map((x: Int) => randomInts(math.pow(2, x + 1).toInt), revOrder(16))

  val timing = map(timeInsertAll(insertAllAVL, 5), data)
  println(timing)
}
{% endhighlight %}

## 5. Proper Testing (Optional, Required for TAs)

Notice that the test above doesn't actually check the results. You could enter
the printed data into a spreadsheet, plot the graph, and examine the result.

Instead, the `cmpsci220.hw.measurement` package has a function to calculate
[linear regression], which lets you fit a line to a set of points.

For example, here is are two real tests that try to fit the output a
a line and a log-curve:

{% highlight scala %}
test("timing insertAllBST on ordered input") {
  // You may need to tweak this data to suit your computer
  val data = map((x: Int) => revOrder(math.pow(2, x + 1).toInt), revOrder(10))
  val timing = map(timeInsertAll(insertAllBST, 5), data)

  val line = linearRegression(timing)
  assert(line.rSquared >= 0.85)
}

def isNonZeroTime(pt: (Double, Double)): Boolean = pt._2 != 0

test("timing insertAllAVL on ordered input") {
  // You may need to tweak this data to suit your computer
  val data = map((x: Int) => revOrder(math.pow(2, x + 1).toInt), revOrder(14))

  val timing = map(timeInsertAll(insertAllAVL, 5), data)
  // Remove points with 0 as the y-coordinate, so that log is defined
  val zeroesRemoved = filter(isNonZeroTime, timing)
  // Put the X-axis on a log scale, so that we can fit a line
  val logScaleX = map((xy: (Double, Double)) => (math.log(xy._1), xy._2), zeroesRemoved)

  val line = linearRegression(logScaleX)
  assert(line.rSquared >= 0.85)
}
{% endhighlight %}

## 6. Check Your Work

Here is a trivial test suite that simply checks to see if you've defined
the `Solution` object with the right type:

{% highlight scala %}
class TrivialTestSuite extends org.scalatest.FunSuite {

  test("The solution object must be defined") {
    val obj : cmpsci220.hw.measurement.MeasurementFunctions = Solution
  }
}
{% endhighlight %}

You should place this test suite in `src/test/scala/TrivialTestSuite.scala`.
If this test suite does not run as-is, you risk getting a zero.

## 7. Submit

*Submission procedure TBD.*

[January 1, 1970]: http://en.wikipedia.org/wiki/Unix_time
[linear regression]: http://en.wikipedia.org/wiki/Simple_linear_regression
[Sets Appeal]: http://papl.cs.brown.edu/2013/sets.html
[Programming and Programming Languages]: http://papl.cs.brown.edu/2013/index.html