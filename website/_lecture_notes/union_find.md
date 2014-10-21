---
layout: lecture
title: Union Find
---

**WARNING**: This code has not been run.

A collection of sets are *disjoint*, if no element occurs in two sets.
These are three *disjoint sets*:

    - {"Amherst", "Boston", "Northampton"}
    - {"New York City", "Albany"}
    - {"Newark"}

But, these three sets are not disjoint (why not?):

    - {"Amherst", "Boston", "Northampton", "Newark"}
    - {"New York City", "Albany"}
    - {"Newark"}

The *disjoint set data structure* (also known as the *union-find* data structure)
is an abstract data type that supports three operations:

{% highlight scala %}
trait DisjointSetLike[A] {

  // Creates a singleton set containing only x. Fails if x is already an element
  // of an existing set.
  def makeSingleton(x: A): Unit

  // Produces true if x and y are in the same set
  def inSameSet(x: A, y: A): Boolean

  // Unifies the sets contains x and y
  def union(x: A, y: A): Unit
}
{% endhighlight %}

Imagine that `DisjointSet` implements `DisjointSetLike`. We could witness
the following operations:

{% highlight scala %}
val sets = new DisjointSet[String]() // empty collection
sets.makeSingleton("Amherst") // {"Amherst"}
sets.makeSingleton("Boston") // {"Amherst"}, {"Boston"}
sets.makeSingleton("Northampton") // {"Amherst"}, {"Boston"}, {"Northampton"}
sets.union("Amherst", "Northampton") // {"Amherst", "Northampton"}, {"Boston"}
sets.union("Amherst", "Boston") // {"Amherst", "Northampton", "Boston"}
sets.inSameSet("Northampton", "Boston") // true
{% endhighlight %}

## A Naive Solution

Here is a very naive way to implement it. We could have a private, mutable map
that maps every element to the set of other values in the same set:

{% highlight scala %}
class NaiveDisjointSet[A]() extends DisjointSetLike[A] {

  private val m = collection.mutable.Map[A, collection.mutable.Set[A]]()

  def makeSingleton(x: A): Unit = {
    require(!m.contains(x))
    m += (x -> collection.mutable.Set[A]())
  }

  def inSameSet(x: A, y: A): Boolean = {
    require(m.contains(x))
    require(m.contains(y))
    m(x).contains(y) // or m(y).contains(x)
  }

  def union(x: A, y: A): Unit = {
    val addToX = m(y) + y
    val addToY = m(x) + x
    m = (x -> m(x) + addToX)
    m = (y -> m(y) + addToY)
    for (x1 <- m(x)) {
      m = (x1 -> m(x1) + addToX)
    }
    for (y1 <- m(y)) {
      m = (y1 -> m(y1) + addToY)
    }
  }
}
{% endhighlight %}

It should be obvious that `union` is extremely naive. We iterate through all
the elements in the same set as `x` and `y`. So, as sets grow larger, it will
take more work to calculate their union.

## Using Directed Graphs

The first key insight is that we will use *directed, acyclic graphs* to
represent sets. In these graphs, an element is a node and an edge between two
nodes means that the they are in the same set. We can't create edges between
strings and other values. So, we'll use a trick: we'll develop a seperate
class for nodes and have `DisjointSet` maintain a map from values to nodes.
For simplicity, every vertex has exactly one outgoing edge to its `parent`.
Vertices also store their value, but this is mostly to aid debugging:

{% highlight scala %}
class Vx[A](value: A) {

  private var parent: Vx[A] = null

  def getParent() = parent
  def setParent(vx: Vx[A]): Unit = { parent = vx }

  override def toString(): String =  s"Vx(${value.toString})"
}
{% endhighlight %}

A `DisjointSet` must now map values to vertices:

{% highlight scala %}
class DisjointSet[A]() extends DisjointSetLike{
  val vertices = collection.mutable.Map[A, Vx[A]]()

  def makeSingleton(x: A): Unit = vertices.get(x) match {
    case Some(_) => throw new IllegalArgumentException(s"$x already in set")
    case None => {
      val vx = new Vx(x)
      vertices += (x -> vx)
    }
  }

  def inSameSet(x: A, y: A): Boolean = { ... }
  def union(x: A, y: A): Unit = { ... }

  override def toString(): String = vertices.toString
}
{% endhighlight %}


### Version 1

In this version, two values are in the same set iff there is an edge between two
nodes.

{% highlight scala %}
class DisjointSet[A]() extends DisjointSetLike{
  val vertices = collection.mutable.Map[A, Vx[A]]()

  def makeSingleton(x: A): Unit = vertices.get(x) match {
    case Some(_) => throw new IllegalArgumentException(s"$x already in set")
    case None => {
      val vx = new Vx(x)
      vertices += (x -> vx)
    }
  }

  def inSameSet(x: A, y: A): Boolean = { a.parent == y  || y.parent == x }
  def union(x: A, y: A): Unit = { x.parent = y }

  override def toString(): String = vertices.toString
}
{% endhighlight %}

But, this doesn't work because we can have a path of length 3 (and other
problems).

### Version 2

In this version, two values are in the same set iff they have a common
ancestor.

{% highlight scala %}
class DisjointSet[A]() extends DisjointSetLike {
  /* omitted fields and methods are as before */

  def vxOldestAncestor(vx: Vx[A]): Boolean = {
    if (vx.getParent == null) {
      vx
    }
    else {
      vxOldestAncestor(vx.getParent)
    }
  }

  def inSameSet(x: A, y: A): Boolean = {
    require(vertices.get(x) != None)
    require(vertices.get(y) != None)
    vxOldestAncestor(vertices(x)).eq(vxOldestAncestor(vertices(y)))
  }

  def union(x: A, y: A): Unit = {
    require(vertices.get(x) != None)
    require(vertices.get(y) != None)
    vxOldestAncestor(vertices(x)).setParent(vxOldestAncestor(vertices(y)))
  }
}
{% endhighlight %}

This is correct, but not so great since the graph can get imbalanced.

### Version 3

Let's update `Vx` to track the size of the graph under it. In `union`,
we'll make the smaller graph a child of the larger graph.

{% highlight scala %}
class Vx[A](value: A) {
  /* omitted fields and methods are as before */

  private var weight_ = 0
  def weight(): Int = weight_
  def addWeight(n: Int): Unit = {
    weight_ = weight_ + n
  }
}

class DisjointSet[A]() extends DisjointSetLike {
  /* omitted fields and methods are as before */

  def union(x: A, y: A): Unit = {
    require(vertices.get(x) != None)
    require(vertices.get(y) != None)
    val x1 = vxOldestAncestor(vertices(x))
    val y1 = vxOldestAncestor(vertices(y))
    if (x1.weight < y1.weight) {
      y1.addWeight(x1.weight)
      x1.setParent(y1)
    }
    else {
      x1.addWeight(y1.weight)
      y1.setParent(x1)
    }
  }

}
{% endhighlight %}

In the code above, since we added weight to `x1` or `y1`, why don't we have
to add weight to their parents? (Answer: because they don't have parents,
since they are oldest ancestors.)

This solution is quite good. In fact, you can prove that the height of the
graph is at most `O(log(n))`. This means that `union` and `inSameSet`
run in time `O(log(n))`.

### Version 4

In this version, we'll implement *path-compression*. When calculating
the oldest ancestor of a vertex, we'll update its parent to be the oldest
ancestor, which further shrinks the path.

{% highlight scala %}
class DisjointSet[A]() extends DisjointSetLike {
  /* omitted fields and methods are as before */

  def vxOldestAncestor(vx: Vx[A]): Boolean = {
    if (vx.getParent == null) {
      vx
    }
    else {
      val parent = vx.getParent
      val oldestAncestor = vxOldestAncestor(parent)
      vx.setParent(oldestAncestor)
      parent.addWeight(-vx.weight)
    }
  }
}
{% endhighlight %}

In the *else*-branch above, notice how the weight of `parent` is reduced, since
`vx` is no longer a child of parent. However, the weight of `oldestAncestor`
is unchanged, since `vx` is still its descendant.

## Final Version

There is a major bug in all the versions that use a directed graph. We can
create a cycle that will send us into an infinite loop. Can you fix it?