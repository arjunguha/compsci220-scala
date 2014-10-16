---
layout: lecture
title: Variance
---

Top and bottom of the type hierarchy are `Any` and `Nothing`.

{% highlight scala %}
sealed trait List[A]
case class Cons[A](head: A, tail: List[A]) extends List[A]
case class Empty[A]() extends List[A]

trait Animal
class Dog extends Animal
class Cat extends Animal
{% endhighlight %}

We can do this:

{% highlight scala %}
Cons(new Cat, Cons(new Dog, Empty()))
{% endhighlight %}

This has type `List[Animal]`. Scala calculates the "upper bound" of the types
`Cat` and `Dog`. `Any` and `Animal` are both valid upper bounds. But, it picks
the least upper bound (usually).

For the following, Scala calculates `List[Cat]`:

{% highlight scala %}
Cons(new Cat, Empty()
{% endhighlight %}

But, we could write this:

{% highlight scala %}
Cons[Animal](new Cat, Empty()
{% endhighlight %}

Scala will upcast the type of `new Cat` to `Animal`.

## Invariance

But, this does not work:

{% highlight scala %}
val lst1: List[Cat] = Cons(new Cat, Empty()
val lst2: List[Animal] = lst2
{% endhighlight %}

Why this restriction?

Consider this container:

{% highlight scala %}
class C[A](var value: A) {
  def set(newValue: A): Unit = { value = newValue }

  def get(): A = value
}

val c: C[Animal] = new C[Animal](new Cat)
val c1: C[Cat] = c
c1.set(new Cat)
c.set(new Dog)
c1.get() // return type Cat, ooops!!
{% endhighlight %}

This is **invariance**.

## Covariance

You cannot write `+A` above. But, the following works:

{% highlight scala %}
sealed trait List[+A]
case class Cons[+A](head: A, tail: List[A]) extends List[A]
case class Empty[+A]() extends List[A]

val lst1: List[Cat] = Cons(new Cat, Empty()
val lst2: List[Animal] = lst2
{% endhighlight %}

This `+A` disallows the method before.

## Scala hack (case objects):

{% highlight scala %}
sealed trait List[+A]
case class Cons[+A](head: A, tail: List[A]) extends List[A]
case object Empty[Nothing] extends List[Nothing]
{% endhighlight %}

Append as a method:

package covariantListsWithApp

sealed trait List[+A] {
  def app[B >: A](other: List[B]): List[B]
}

case object Empty extends List[Nothing] {
  def app[B](other: List[B]): List[B] = other
}

case class Cons[+A](head: A, tail: List[A]) extends List[A] {
  def app[B >: A](other: List[B]): List[B] = Cons(head, tail.app(other))
}