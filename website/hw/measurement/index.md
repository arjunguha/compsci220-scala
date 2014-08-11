---
layout: hw
title: Measurement
---

Introduction
------------

For this assignment, you will measure the time it takes to execute typical
operations on several data structures: ordered association lists, (unbalanced)
binary search trees, and AVL trees. We provide the data structures, but
you have to develop the benchmarking framework.

Timing Functions
----------------

<ol>

<li>Write a higher-order function <code>time(f, a)</code> that calculates
the time needed to execute <code>f(a)</code> in *milliseconds*.

{% highlight scala %}
def time[A, B](f : A => B, a : A) : Long
{% endhighlight %}

Use the the builtin function
<code>System.currentTimeMillis()</code>, which returns the current time in
milliseconds (since [January 1, 1970]). you </li>

<li>Write a higher-order function <code>averageTime(n, f, a)</code> that
calculates the time needed to execute <code>f(a)</code> in milliseconds,
averaged over <code>n</code> executions.

{% highlight scala %}
def averageTime[A, B](trials : Int, f : A => B, a : A) : Double
{% endhighlight %}

</li>

</ol>

An easy way to test these functions is to use <code>Thread.sleep(n)</code>,
which pauses execution for <code>n</code> milliseconds. Therefore,
<code>time((n:Int) => Thread.sleep(n), n)</code> should be approximately
<code>n</code>.

Test Data Generation
--------------------

<ol start="3">

<li>Write a recursive function <code>iota(n)</code> that produces the list
of <code>n</code> consecutive integers, starting from <code>0</code> and ending
at <code>n - 1</code>.

{% highlight scala %}
def iota(n : Int) : List[Int]
{% endhighlight %}

</li>

<li>Write a recursive function <code>randomInts(n)</code> that produces a list of
<code>n</code> randomly chosen integers.

{% highlight scala %}
def randomInts(n : Int) : List[Int]
{% endhighlight %}

You will need to use the builtin function <code>util.Random.nextInt()</code>
which returns a randomly chosen integer.
</li>

</ol>

Test Cases
----------

The [cmpsci220.hw.measurement] package defines several functions to add and
lookup values individual elements in sorted lists, binary search trees, and AVL
trees.

<ol start="5">

<li>Write functions to insert and lookup lists of elements:

{% highlight scala %}
// Map all keys to the provided value.
def insertAllOrdList(lst : List[Int]) : List[Int]
def insertAllBST(lst : List[Int]) : Tree[Int]
def insertAllAVL(lst : List[Int]) : Tree[Int]

// Returns true only if all keys are mapped to the provided value.
def isMemberAllOrdList(lst : List[Int]) : Boolean
def isMemberAllBST(tree : Tree[Int]) : Boolean
def isMemberAllAVL(tree : Tree[Int]) : Boolean
{% endhighlight %}
</li>

<li>
All the `insert` functions have the same pattern. The only variation is
the type of the map and the type-dependent `empty` value and `insert` function.
The `lookup` functions also follow the same pattern. Therefore, instead of
writing repetitive code, write the following higher-order functions and use
them to define the functions above:


{% highlight scala %}
def insertAll[T](empty : T, insert : (Int, T) => T, values : List[Int]) : T =

def isMemberAll[T](isMember : (Int, T) => Boolean, values : List[Int], set : T) : Boolean
{% endhighlight %}
</li>

</ol>

Benchmarking
------------


def benchmarkAssocList(keys : List[

averageTime(


returns the average time needed to execute

[January 1, 1970]: http://en.wikipedia.org/wiki/Unix_time


