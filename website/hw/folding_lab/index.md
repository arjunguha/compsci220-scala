---
layout: hw
title: Recursion Lab 2 (Folding)
---

<a href="http://xkcd.com/1270/">
<img src="http://imgs.xkcd.com/comics/functional.png">
</a>

The purpose of this lab is to help further develop your recursion and
list-handling skills. You may work in pairs if you do not have a laptop. Both of
you should submit the same code file with BOTH of your names on it.
For this lab, we'll be using the List[A] case class, which you are already
familiar with.

**Please submit whatever you finish to Moodle.**

The template file for this lab is available [here](foldinglab.scala).

## Preliminaries

Save your work in a file called `foldinglab.scala`.

Start your program with these lines:

{% highlight scala %}
import cmpsci220._
import cmpsci220.hw.recursion._
{% endhighlight %}

For this lab, we'll be using the List[A] case class, which you are already
familiar with.

## Question 1:

Write the `sum` and `product` functions. These take a list of integers and
returns the sum and product, respectively, of the elements.

{% highlight scala %}
def sum(lst: List[Int]): Int

test("Sum test: Empty list") {
    assert(sum(List()) == 0)
}

test("Sum test: Short list") {
    assert(sum(List(1,2,3)) == 6)
}
{% endhighlight %}

{% highlight scala %}
def product(lst: List[Int]): Int

test("Product test: Empty list") {
    assert(product(List()) == 1)
}
test("Product test: Short list") {
    assert(product(List(1,2,3,4)) == 24)
}
{% endhighlight %}

## Question 2:
Rewrite the `sum' function (call it `baseSum') so that it returns a given result
for empty lists.

{% highlight scala %}
def baseSum(onEmpty: Int, lst: List[Int]): Int

test("Sum with base: Empty list") {
    assert(baseSum(-1, List()) == -1)
}

test("Sum with base: Short list") {
    assert(baseSum(-1, List(1,2,3)) == 5)
}
{% endhighlight %}

## Question 3:
Write a function that takes an integer operation, a value for empty lists,
and a list of integers and returns the result of cumulatively applying the
operation to all elements of the given list.

{% highlight scala %}
def genOp(op: ((Int, Int) => Int), onEmpty: Int, lst: List[Int]): Int

test("genOp: Same as sum") {
    assert(genOp(+, 0, List(10, 20, 30)) == sum(10,20,30))
}

test("genOp: Same as product") {
    assert(genOp(*, 1, List(10,11,12)) == product(10,11,12))
}
{% endhighlight %}

## Question 4:
Write the `fold' function which takes a function of two arguments, a value for
empty lists, and a list of the function's input type and returns the result
of cumulatively applying the function to each element of the list.

{% highlight scala %}
def fold[A](op: ((A,A) => A), onEmpty: A, lst: List[A]): A

test("fold: Same as sum") {
    assert(fold(+, 0, List(10,20,30)) == sum(10,20,30))
}

test("fold: String test") {
    assert(fold(+, "", List("Hello", ", ", "world", "!")) == "Hello, world!")
}
{% endhighlight %}

## Question 5:
Write the `fold2' function, which takes a function ((A, B) => B), a value for
empty lists, and a list of the function's input type. It returns the result
(of type B) of cumulatively applying the function to the elements of the
list.

{% highlight scala %}
def fold2[A,B](op: ((A,B) => B), onEmpty: B, lst: List[A]): B

test("fold2: String lengths") {
    val op = (s: String, x: Int) => x + s.length
    assert(fold2(length, 0, List("Hello", ", ", "world", "!")) == 0)
}
{% endhighlight %}
