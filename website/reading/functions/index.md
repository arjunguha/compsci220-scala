---
layout: lecture
title: Functions
---

We've seen that functions in Scala can appear in almost any place. For example,
we've seen nested functions:

{% highlight scala %}
def doubleAll(lst: List[Int]) = {
  def f(n: Int): Int = n * 2
  map(f, lst)
}
{% endhighlight %}

We've seen functions that take other functions as arguments, such as `map`:

{% highlight scala %}
def map[A,B](f: A => B, lst: List[A]): List[B] = lst match {
  case Empty() => Empty[B]()
  case Cons(head, tail) => Cons[B](f(head), map[A,B](f, tail))
}
{% endhighlight %}

And, we've even see that functions can produce other functions:

{% highlight scala %}
def compose[A,B,C](f: A => B, g: B => C) : A => C = {
  def h(a: A): C = { g(f(a)) }
  h
}
{% endhighlight %}

Here is an even simpler example of a function that produces another function:

{% highlight scala %}
def makeAdder(x: Int): Int => Int = {
  def addX(y: Int): Int = x + y
  addX(x)
}

val addThree = makeAdder(3)
test("add3 test") {
  assert(addThree(10) == 13)
}
{% endhighlight %}

Functions can even be stored in data structures. For example, consider
this simple, recursive evaluator for arithmetic expressions:

{% highlight scala %}
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(e1: Expr, e2: Expr) extends Expr
case class Mul(e1: Expr, e2: Expr) extends Expr

def eval(e: Expr): Expr = e match {
  case Num(n) => n
  case Add(e1, e2) => eval(e1) + eval(e2)
  case Mul(e1, e2) => eval(e1) * eval(e2)
}
{% endhighlight %}

Imagine extending this evaluator to support subtraction, division, etc.
We could do so by adding more cases to `Expr` and adding corresponding
lines to `eval`. But, this will quickly become tedious and repetitive.

But, since we can store functions in data structures, we can replace
`Add`, `Mul`, `Div`, `Sub`, `Exp`, and all other binary operators with
a single constructor for binary arithmetic expressions:

{% highlight scala %}
sealed trait Expr
case class Num(n: Int) extends Expr
case class BinOp(op: (Int, Int) => Int, e1: Expr, e2: Expr) extends Expr

def eval(e: Expr): Expr = e match {
  case Num(n) => n
  case BinOp(op, e1, e2) => op(eval(e1), eval(e2))
}
{% endhighlight %}

With these definitions, we can represent any binary arithmetic expression
we please:

{% highlight scala %}
def sub(x: Int, y: Int): Int = x - y

test("subtraction test") {
  assert(eval(BinOp(sub, 10, 3)) == 7)
}
{% endhighlight %}

## Anonymous Functions

In Scala, **functions are values**, just like integers, strings, or any user-
defined type. They can truly appear in all the contexts in which other values
appear: as arguments, as results, as fields in a data-structure, and so on.

Unlike other values, functions seem to have the following special property:
every function has a name, but the other kinds of values do not.
For example, we can simply write `Cons(1, Cons(2, Cons(3, Empty())))`
and don't need to give this list a name. But, all functions
start with `def functionName`.

But, this is just convenience and a convention. Just like other values,
functions don't need names. For example, here is a function that adds two
numbers:

{% highlight scala %}
((x: Int, y: Int) => x + y)
{% endhighlight %}

This function does not have a name, but it can be applied just like
any other function:

{% highlight scala %}
((x: Int, y: Int) => x + y)(10, 20)
{% endhighlight %}

The code above is not easy to read. It will be a lot clearer of we give
the function a name. You already know how to name a function using `def`.
But, all other values are named using `val`. In fact, we can use `val` to name
functions too, just like any other type of value:

{% highlight scala %}
val adder = ((x: Int, y: Int) => x + y)

adder(10, 20)
{% endhighlight %}

You should think of `def` as a convenient shorthand for naming functions.
That is, these two definitions are equivalent:

{% highlight scala %}
val adder = ((x: Int, y: Int) => x + y)

def adder(x: Int, y: Int) = x + y
{% endhighlight %}

In general, it is a good idea to name your functions. But, there are
certain situations where a short, anonymous function can made your code
easier to read and write.

For example, we earlier defined the `doubleAll` function, which doubles
every number in a list of numbers. Here is simple, one-line definition
using an anonymous function as an argument to `map`:

{% highlight scala %}
def doubleAll(lst: List[Int]) = map((n: Int) => n * 2, lst)
{% endhighlight %}

The anonymous function itself is extremely simple and the name of the enclosing
function, `doubleAll`, really makes it clear what it does.

For another example, suppose we want to remove all the odd numbers
in a list. We can do this using `filter` and a short, anonymous function:

{% highlight scala %}
def removeOdds(lst: List[Int]) = filter((n: Int) => n % 2 == 0, lst)
{% endhighlight %}

In these kinds of situations, anonymous functions can be very helpful.

## Some Definitions

Here are some terminology that gets thrown around when comparing programming
languages and programming techniques.

- **Higher-order functions** are functions that consume or return other
  functions   as values. You can tell if a function is higher-order by inspecting
  its type. Does it have any nested `=>`s in the type? If so, it is a higher-order
  function.

  But, what about the `eval` function we wrote above, which consumes `BinOp`s?
  Is that a higher-order function?

- **First-class functions** is a property of a programming language. For
  a programming language to have first-class functions, it must treat
  functions as values, with all the rights and privileges that other values
  have. You must be able to use functions as arguments, produce
  functions as results, and store functions in data-structures.


