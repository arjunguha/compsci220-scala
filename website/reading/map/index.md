---
layout: lecture
title: Map In Depth
---

In the last chapter, we defined the `List[A]` type and several generic list-
processing functions, including `map`. Here is the definition of `map`
from the last chapter. The `map` function applies `f` to every element
in a list and produces the list of results:

{% highlight scala %}
def map(f: Int => Int, lst: List[Int]): List[Int] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => Cons(f(head), map(f, tail))
}
{% endhighlight %}

In this chapter, we'll see how to define `map` for an arbitrary container
data structure.

## Binary Trees

Let's define a type for binary trees called `Tree[A]`. We are *not* trying to
define binary search trees, so we don't have to think about keys and values
or orderings. We simply need a way to represent tree-shaped data.
There are several ways to represent binary search trees. For this example,
let's consider trees that have values at interior nodes and no values
at the leaves:

{% highlight scala %}
sealed trait Tree[A]
case class Leaf[A]() extends Tree[A]
case class Node[A](lhs: Tree[A], value: A, rhs: Tree[A]) extends Tree[A]
{% endhighlight %}

For example, the following tree, where `.` represents a leaf:

<pre>
       5
      / \
     7   .
    / \
   4   .
  / \
 .   .
</pre>

Can be represented as like this:

{% highlight scala %}
val tree1 = Tree(Tree(Tree(Leaf(), 4, Leaf()), 7, Leaf()), 5, Leaf())
{% endhighlight %}

### `map` for Binary Trees

What does it mean to define a `mapTree` function for binary tree?. Just like
`map` over lists, `mapTree` will apply a function `f` to every value in
the tree. `mapTree` will also preserve the structure of the tree, just
like `map` did for lists. We can write `mapTree` by simply recurring into every sub-tree,
just as we did for map:

{% highlight scala %}
def mapTree[A, B](f: A => B, tr: Tree[A]): Tree[B] = tr match {
  case Leaf() => Leaf()
  case Node(l, v, r) => Node(mapTree(f, l), v, mapTree(f, r))
}
{% endhighlight %}

For example, suppose we want to add `1` to every value in a tree of integers,
such as `tree1`. We could use `mapTree` to do this very easily:

{% highlight scala %}
def add1(n: Int): Int = n + 1

def incrTree(tr: Tree[Int]): Tree[Int] = mapTree(add1, tr)
{% endhighlight %}


## `Option[A]` is a container

We can define a `map`-like function for any container type that is parameterized
over the type of value it stores. In fact, we even treat the `Option[A]` type
as a container. Previously, we've treated `Option[A]` as an alternative to
exceptions. However, we can also think of it as an container. Unlike
`List[A]` and `BinTree[A]`, which can hold several values, an `Option[A]`
can only hold zero values or one value.

Here is `mapOption`:

{% highlight scala %}
def mapOption[A, B](f: A => B, opt: Option[A]): Option[B] = opt match {
  case None() => None()
  case Some(x) => Some(f(x))
}
{% endhighlight %}

Notice that it has exactly the same structure as `map` and `mapTree`: it
applies `f` to the value of type `A`. However, it does not need to
recur, since `Option[A]` is not a recursively-defined type.

## Some stranger containers

There is nothing special about the types `List[A]`, `BinTree[A]`, and
`Option[A]`. We could define `map` for arrays, hash-tables, or anything else.

We can also define some strange containers. For example, here is a container
that holds one or two values:

{% highlight scala %}
trait OneTwo[A]
case class One[A](x: A) extends OneTwo[A]
case class Two[A](x: A, y: A) extends OneTwo[A]
{% endhighlight %}

Can you define a `map`-like function for this container? It will look a lot like
`mapOption`, since you don't need to recur.

Here is another strange container that only stores an even number of elements:

{% highlight scala %}
trait EvenList[A]
case class EmptyEvenList[A]() extends EvenList[A]
case class ConsEvenList[A](x: A, y: A, rest: EvenList[A]) extends EvenList[A]
{% endhighlight %}

Can you define a `mapEvenList` function?

## What does `map`-like really mean?

We've seen several `map`-like functions for different data structures, but what
does it really mean for a function to be `map`-like?

To help us answer this question, we'll have to use the best generic function
in the world:

{% highlight scala %}
def id[A](x: A) = x
{% endhighlight %}

In the definition above, `id` is short for *the identity function*, but you
already knew that.

We have used `map` with several functions: the increment function, which adds 1
to an integer, the double function, the string-length function, etc. But,
what happens if we map the identity function over a list?

{% highlight scala %}
map(id, lst)
{% endhighlight %}

Unsurprisingly, the following identity holds `map(id, lst) == lst` (no pun
intended).

This identity holds for the other `map`-like functions too:

{% highlight scala %}
mapTree(id, tree) == tree
mapOption(id, opt) == opt
{% endhighlight %}

This identity should hold for the `map`-like functions over `EvenList` and
`OneTwo`types too.

Here is another property of `map`. Consider the following code:

{% highlight scala %}
def double(x: Int): Int = x + x

def add1(x: Int): Int = x + 1

val lst = List(1, 2, 3)

map(add1(map(double, lst)))
{% endhighlight %}

This program first doubles every value in `lst`, producing the list
`List(2, 4, 6)` and then adds 1 to every value in this intermediate list, producing
`List(3, 5, 7)`.

Instead of producing the intermediate list, we could simply write a composite
function that both doubles and increments:

{% highlight scala %}
def doubleThenAdd1(x: Int): Int = (x + x) + 1

map(add1(map(double, lst))) == map(DoubleThenAdd1, lst)
{% endhighlight %}

We can generalize this observation if we write a function to compose two
functions:

{% highlight scala %}
def compose[A,B,C](f: A => B, g: B => C) : A => C = {
  def h(a: A): C = { g(f(a)) }
  h
}

map(g, map(f, lst)) == map(compose(f, g), lst)
{% endhighlight %}

This identity also holds for the other `map`-like functions:

{% highlight scala %}
mapTree(g, mapTree(f, tree)) == mapTree(compose(f, g), tree)
mapOption(g, mapOption(f, opt)) == mapOption(compose(f, g), opt)
{% endhighlight %}

It is quite easy to think of other simple properties of `map`. For example,
if we have a function to append two lists:

{% highlight scala %}
def append[A](lst1: List[A], lst2: List[A]): List[A]
{% endhighlight %}

Then, this identity holds:

{% highlight scala %}
append(map(f, lst1), map(f, lst2)) == map(f, append(lst1, lst2))
{% endhighlight %}

However, it is not clear what it means to `append` two binary trees. So, perhaps
this identity only makes sense for lists.

However, the two earlier identities seem to hold for any type of container:

{% highlight scala %}
map(g, map(f, container)) == map(compose(f, g), container)

map(id, container) == id
{% endhighlight %}

Perhaps what means for a function to be `map`-like is that these two identities
must hold.

We won't answer this question in this class, since that would take us into
the realm of [category theory]. But, you should realize that we can define
`map` for any sort of container.

[category theory]: http://en.wikipedia.org/wiki/Functor