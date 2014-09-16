---
layout: hw
title: Recursion
---

<a href="http://xkcd.com/244/">
<img src="http://imgs.xkcd.com/comics/tabletop_roleplaying.png">
</a>

This assigment has several independent exercises that will develop your
grasp of recursion, generics, and higher-order functions.


## Preliminaries

Get the software update:

{% highlight scala %}
sudo apt-get update
sudo apt-get upgrade cs220
sudo docker.io pull arjunguha/cs220
{% endhighlight %}

You may neeed to restart the virtual machine.

Save your work in a file called `recursion.scala`.

Start your program with these lines:

{% highlight scala %}
import cmpsci220._
import cmpsci220.hw.recursion._
{% endhighlight %}

The latter line imports the types and some useful functions that you will use
in this assignment.

## Part 1. Lists

Write the `map2` function, which maps over two lists:

{% highlight scala %}
def map2[A,B,C](f: (A, B) => C, lst1: List[A], lst2: List[B]): List[C]

test("map2 with add") {
  def add(x: Int, y: Int): Int = x + y
  assert(map2(add, List(1, 2, 3), List(4, 5, 6)) == List(5, 7, 9))
}
{% endhighlight %}

You may assume that `lst1` and `lst2` have the same length (i.e., do whatever
you think is reasonable).

Write the `zip` function, which pairs corresponding elements in a list:

{% highlight scala %}
def zip[A,B](lst1: List[A], lst2: List[B]): List[(A, B)]

test("zip test") {
  assert(zip(List(1, 2, 3), List(4, 5, 6)) == List((1,4), (2, 5), (3, 6)))
}
{% endhighlight %}

*Hint: You can write `zip` using `map2`.*

Write the function `flatten`, which flattens a nested list:

{% highlight scala %}
def flatten[A](lst: List[List[A]]): List[A]

test("flatten test") {
  assert(flatten(List(List(1, 2), List(3, 4))) == List(1, 2, 3, 4))
}
{% endhighlight %}

*Hint: You need to write another function before you can write `flatten`.*

Write the `flatten3` function, which flattens a triple-nested list:

{% highlight scala %}
def flatten3[A](lst: List[List[List[A]]]): List[A]
{% endhighlight %}

*Hint You can write `flatten3` using `flatten`.*

Write the `buildList` function, which builds a list of the given length. Each
element is determined by applying `f` to the index of the element:

{% highlight scala %}
def buildList[A](length: Int, f: Int => A): List[A]

test("buildList test") {
  def f(x: Int) = x
  assert(buildList(10, f) == List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
}
{% endhighlight %}

*Hint: You need to write a helper function.*

Write the `mapList` function, which maps each element to a list and returns
the list of all results:

{% highlight scala %}
def mapList[A, B](lst: List[A], f: A => List[B]): List[B]

test("mapList test") {
  def f(n: Int): List[Int] = buildList(n, (_: Int) => n)
  assert(mapList(List(1, 2, 3), f) == List(1, 2, 2, 3, 3, 3))
}
{% endhighlight %}

*Can you write `mapList` without directly using recursion? i.e., just using the
other functions defined above? (You actually can.)*

**Check Your Work:** From the command-line, run:

    check220 check recursion step1

## Part 2. Persistent Queues

Recall from earlier classes, that a *queue* is a data structure that supports
three operations:

- *empty* constructs an empty queue

- *enqueue* adds a new element to the back of the queue

- *dequeue* removes an element from the back of the queue, if the queue is
  not empty

In the following exercises, you will build a *persistent queue*. A persistent
queue has the operations defined. But, instead of having enqueue and dequeue
update the queue, they leave the original queue unchanged and return a new
queue.

It is easy to implement a persistent queue using a list:

{% highlight scala %}
type SlowQueue[A] = List[A]

def emptySlow[A](): SlowQueue[A] = Empty()

def enqueueSlow[A](elt: A, q: SlowQueue[A]): SlowQueue[A] = q match {
  case Empty() => List(elt)
  case Cons(head, tail) => Cons(head, enqueueSlow(elt, tail))
}

def dequeueSlow[A](q: SlowQueue[A]): Option[(A, SlowQueue[A])] = q match {
  case Empty() => None()
  case Cons(head, _) => Some((head, tail))
}
{% endhighlight %}

Read the code above carefully. The *enqueue* operation traverses the
entire list each time (i.e., *O(n)* running time). Your task is to implement
the queue more efficiently.

The trick is to represent the queue using two lists. The first list, called
*front*, has the elements at the front of the queue. The second list, called
*back*, has the elements at the back of the queue, **in reverse order**.

For example, if *front* is `List(1, 2, 3)` and *back* is `List(6, 5, 4)`, then
the elements of the queue, in order, are 1, 2, 3, 4, 5, 6. With this
representation:

- *enqueue* adds an element to *back*, but doesn't need to traverse the whole
  list.

- *dequeue* removes an element from *front*, unless *front* is empty. If it is
  empty, it reverses *back* and uses it as the front.

The [cmpsci220.hw.recursion] library defines a type called `Queue`, which has two
*front* and *back*. Using this type, define the following functions:

{% highlight scala %}
def enqueue[A](elt: A, q: Queue[A]): Queue[A]

def dequeue[A](q: Queue[A]): Option[(A, Queue[A])]
{% endhighlight %}

*Hint*: `dequeue` needs the `reverse` function, which is defined in
[cmpsci220.hw.recursion].

**Check Your Work:** From the command-line, run:

    check220 check recursion final

## Hand In

From the command-line, run the following command:

    check220 tar recursion final


[cmpsci220.hw.recursion]: ../../lib/api/#hw.recursion.package