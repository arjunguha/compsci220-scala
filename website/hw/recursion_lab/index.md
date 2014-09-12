---
layout: hw
title: Recursion lab
---

<a href="http://xkcd.com/244/">
<img src="http://imgs.xkcd.com/comics/tabletop_roleplaying.png">
</a>

This assigment has several independent exercises that will develop your
grasp of recursion, generics, and higher-order functions.


## Preliminaries

Save your work in a file called `recursionlab.scala`.

Start your program with these lines:

{% highlight scala %}
import cmpsci220._
import cmpsci220.hw.recursion._
{% endhighlight %}

The second line imports the types that you will use in this assignment. You've
already seen the types `List[A]` and `Option[A]` in class. But,
the type `Queue[A]`, which is used in the last set of exercises, is new.

## Question 1:

Write the `forall` function, which returns true if all elements of list satisfy the function:

{% highlight scala %}
def forall[A](f: A => Boolean, lst:List[A]): Boolean

test("forall test1") {
  def f(n: Int) :Boolean = {
    n % 2 == 0
  }
  val lst = List(2,4,6,8,12)
  assert(forall(f, lst))
}

test("forall test2") {
  def f(n: String) :Boolean = {
    n.length % 2 == 0
  }
  val lst = List("hello!","Good")
  assert(forall(f, lst))
}

{% endhighlight %}

## Question 2:
Write the `findlast` function, which returns the last element of a list that returns true for a function:

{% highlight scala %}
def findlast[A] (f: A => Boolean, lst : List[A]) : Option[A]

test("findlast test1") {
  def f(n:Int): Boolean = {
    n % 2 == 0
  }
  val lst = List(200, 100)
  assert(findlast(f, lst) == Some(100))
}

test("findlast test2") {
  def f(n:String): Boolean = {
    n.length % 2 == 0
  }
  val lst = List("Welcome","Hey!","Goodbye","Goodbye!")
  assert(findlast(f, lst) == Some("Goodbye!"))
}

{% endhighlight %}

*Hint: You can write `findlast` as a recursive call. This recursive call can be unconditional too*

[cmpsci220.hw.recursion]: ../../lib/api/#hw.recursion.package
