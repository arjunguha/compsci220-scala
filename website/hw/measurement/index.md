---
layout: hw
title: Measurement
---

For this assignment, you will measure the time it takes to execute typical
operations on several represetations of integer-sets: ordered association lists, (unbalanced)
binary search trees, and AVL trees. We provide the data structures, but you have
to develop the benchmarking framework.

## Preliminaries

Get the software update:

{% highlight scala %}
sudo apt-get update
sudo apt-get upgrade cs220
sudo docker.io pull arjunguha/cs220
{% endhighlight %}

You may neeed to restart the virtual machine.

Save your work in a file called `measurement.scala`.

Start your program with these lines:

{% highlight scala %}
import cmpsci220._
import cmpsci220.hw.measurement._
{% endhighlight %}


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
  assert(revOrder(1) == List(5, 4, 3, 2, 1))
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
test("timing insertAllOrdList on ordered input") {
  // You may need to tweak this data to suit your computer
  val data = List(revOrder(500), revOrder(1000), revOrder(2000), revOrder(4000))
  val trials = 10
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllOrdList, values)), data)
  println(results)
}
{% endhighlight %}

## 5. Proper Testing (Optional, Required for TAs)

Notice that the test above doesn't actually check the results. The
`cmpsci220.hw.measurement` package includes two functions to calculate
[linear regression], which lets you fit a line to some data, e.g., the
data from the benchmark above. The

For example, here is a real test of `insertAllBST`, which ensures that
the behavior is actually linear:

{% highlight scala %}
test("timing insertAllOrdList on ordered input") {
  // You may need to tweak this data to suit your computer
  val data = List(revOrder(500), revOrder(1000), revOrder(2000), revOrder(4000))
  val trials = 10
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllOrdList, values)), data)
  val (slope, intercept, rSq) = linearRegression(results)
  assert (math.abs(1.0 - rSq) <= 0.1)
}
{% endhighlight %}

[January 1, 1970]: http://en.wikipedia.org/wiki/Unix_time
[linear regression]: http://en.wikipedia.org/wiki/Simple_linear_regression

