---
layout: lecture
title: Programming with Lists
---

We can define a type for lists by cases:

{% highlight scala %}
sealed trait List[A]
case class Cons[A](head: A, tail: List[A]) extends List[A]
case class Empty[A]() extends List[A]
{% endhighlight %}

Just like `Option`, `List` also takes a type-argument, which is the type of
every element in the list.

## The `map` function

Here is a simple function that adds one to every element in a list of integers:

{% highlight scala %}
def add1ToAll(lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(head + 1, add1ToAll(tail))
}
{% endhighlight %}

Here is another function that calculates the lengths of a list of strings:

{% highlight scala %}
def lengthAll(lst: List[String]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(head.length, doubleAll(tail))
}
{% endhighlight %}

Here is another funciton that doubles every element in a list of integers:

{% highlight scala %}
def doubleAll(lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(head * 2, doubleAll(tail))
}
{% endhighlight %}

Hopefully, you've noticed that these three functions have a lot of in common.
The only difference between them is the operation that they perform on the
`head`.

To make the operation explicit, here is a variant of `doubleAll`:

{% highlight scala %}
def f(n: Int): Int = n * 2

def doubleAll(lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(f(head), doubleAll(tail))
}
{% endhighlight %}

In this version, we're simply applying a function `f` to `head`, where `f`
is the doubling function. *You should try to apply the same refactoring to the other
functions:* instead of directly writing `head.length` or `head + 1`, let the
expression be `f(head)`.

Once we've re-written the operation on `head` as `f(head)`, all three functions
look identical: the only different that that each refers to a different function
`f`.

Instead of writing three functions that are almost identical, we can
write one function that takes `f` as an argument:

{% highlight scala %}
def map(f: Int => Int, lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(f(head), map(f, tail))
}
{% endhighlight %}

Note that we had to modify the recursive call to `map(f, tail)` too. With
this function, we can make our three example functions much more succinct:

{% highlight scala %}
def doubleAll(lst: List[Int]) = {
  def f(n: Int): Int = n * 2
  map(f, lst)
}

def add1ToAll(lst: List[Int]) = {
  def f(n: Int): Int = n + 1
  map(f, lst)
}

def lengthAll(lst: List[String]) = {
  def f(str: String): Int = str.length
  map(f, lst)
}
{% endhighlight %}

Unfortunately, the definition of `lengthAll` above does not type-check. Scala
reports two type errors and they are both very informative:

    <console>:15: error: type mismatch;
     found   : String => Int
     required: Int => Int
             map(f, lst)
                 ^
    <console>:15: error: type mismatch;
     found   : List[String]
     required: List[Int]
             map(f, lst)
                    ^

The `map` function only works of lists of integers. However, if you look
at the definition of `map` closee, you'll see that all the `Int`-specific
code have been factored out into `f`. We can make make even more generic
by introducing two type-parameters:

{% highlight scala %}
def map[A,B](f: A => B, lst: List[A]): List[B] = lst match {
  case Empty() => Empty[B]()
  case Cons(head, tail) => Cons[B](f(head), map[A,B](f, tail))
}
{% endhighlight %}

The type-parameter `A` is the type of the elements in `lst` and the type-
parameter `B` is the type elements in the produced list. In the code above,
we've make all the type-arguments explicit. But, as we've discussed before, we
can rely on type-inference to fill them in:

{% highlight scala %}
def map[A,B](f: A => B, lst: List[A]): List[B] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(f(head), map(f, tail))
}
{% endhighlight %}

## The `filter` function

Another common pattern when programming with lists is to select certain elements
that have some property. For example, here is a function consumes a list, `lst`
and produces a new list that only contains the even numbers in `lst`:

{% highlight scala %}
def filterEven(lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) =>
    (head % 2 == 0) match {
      case true => Cons(head, filterEven(tail))
      case false => filterEven(tail)
    }
}
{% endhighlight %}

This is a very common pattern too. For example, we could write a function to
select the odd numbers or the prime numbers. If we had a list of strings, we
could select all the strings with length 5 or all the strings that represent
English-language words. All these functions have the same shape: they test
the value of `head` in some way. If the test succeeds, `head` is added in
the output list. But, if the test fails, it is excluded.

Following the same strategy we used to derive `map`, we first package
the `head`-test into a function:

{% highlight scala %}
def f(n: Int): Boolean = head % 2

def filterEven(lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) =>
    f(head) match {
      case true => Cons(head, filterEven(tail))
      case false => filterEven(tail)
    }
}
{% endhighlight %}

Now that the pattern is clearer, we generalize `filterEven` to take `f`
as an argument:

{% highlight scala %}
def filter(f: Int => Boolean, lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) =>
    f(head) match {
      case true => Cons(head, filter(f, tail))
      case false => filter(f, tail)
    }
}

def filterEven(lst: List[Int]): List[Int] = {
  def f(n: Int): Boolean = head % 2
  filter(f, lst)
{% endhighlight %}

Finally, just as we did for `map`, we can generalize the type of `filter`
so that it can be applied to `List[A]`:

{% highlight scala %}
def filter[A](f: A => Boolean, lst: List[Int]): List[A] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) =>
    f(head) match {
      case true => Cons(head, filter(f, tail))
      case false => filter(f, tail)
    }
}
{% endhighlight %}

