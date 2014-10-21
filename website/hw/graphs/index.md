---
layout: hw
title: Graph Algorithms
---

<a href="http://xkcd.com/246/">
<img src="http://imgs.xkcd.com/comics/labyrinth_puzzle.png">
</a>


This assignment has three main objectives:

- You will implement several, fundamental graph algorithms. By the time you're
  applying for jobs, you should be able to hack these in your sleep.
- You will work with an *abstract data type* for graphs that we provide.
  You will not have to worry about how graphs are implemented, though
  we will discuss several implementations in class.
- You will start using Scala's built-in collection classes. These are
  discussed in the Odersky textbook and there is a lot of information about
  them on the Web.

We will briefly discuss these algorithms in class. But, you will have to use
the Web and other books for detailed descriptions. In fact, I encourage
you to find pseudocode on the Web and translate it to Scala.

## Preliminaries

You should create a series of directories that look like this:

    ./graphs
    |-- build.sbt
    `-- project
       `-- plugins.sbt
    `-- src
       |-- main
       |   `-- scala
       |       `-- your solution goes here
       `-- test
          `|-- scala
               |-- TrivialTestSuite.scala
               `-- your tests goes here

The `build.sbt` file should have exactly this line:

{% highlight scala %}
name := "graphs"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

The `plugins.sbt` file should have exactly this line:

{% highlight scala %}
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "2.2")
{% endhighlight %}

Finally, here is `TrivialTestSuite.scala`:

{% highlight scala %}
class TrivialTestSuite extends org.scalatest.FunSuite {

  test("The solution object must be defined") {
    val obj : cmpsci220.hw.graph.GraphAlgorithms = Solution
  }
}
{% endhighlight %}


## The Graph Abstract Data Type

In this assignment, you're working with an abstract data type for graphs,
`Graph[N,E]`, where `N` is the type of nodes and `E` is the type of edges.
The constructor for `Graph` creates an empty graph:

{% highlight scala %}
import cmpsci220.hw.graph._

val g = new Graph[String, Double]()
{% endhighlight %}

Graphs have methods to create nodes and edges:

{% highlight scala %}
g.mkNode("Amherst")
g.mkNode("Northampton")
g.mkEdge("Amherst", 7.9, "Northampton")
{% endhighlight %}

These methods return `true` if they successfully create a node or edge and otherwise
return `false`. You should read the API documentation to understand exactly why
`mkNode` and `mkEdge` may return `false`. But, here is a simple example:
nodes must be created with `mkNode` before they are used in `mkEdge`:

{% highlight scala %}
g.mkEdge("Northampton", 19.5, "Springfield") // returns false
g.mkNode("Springfield")
g.mkEdge("Northampton", 19.5, "Springfield") // returns true
{% endhighlight %}

These graphs have **no self-loops**:

{% highlight scala %}
g.mkEdge("Northampton", 0, "Northampton") // returns false
{% endhighlight %}

Given two nodes, you can query the label on the edge between them:

{% highlight scala %}
g.getEdge("Amherst", "Northampton") // returns 7.9
{% endhighlight %}

These graphs are **undirected**, so the following query also returns `7.9`:

{% highlight scala %}
g.getEdge("Northampton", "Amherst") // returns 7.9
{% endhighlight %}

From a node, you can also query its neighbors:

{% highlight scala %}
g.neighbors("Northampton") // return Set("Amherst", "Springfield")
{% endhighlight %}

The easiest way to create a simple graph is from an edge-list:

{% highlight scala %}
val g2 = Graph(("Amherst", 7.9, "Northampton"),
               ("Amherst", 19.5, "Springfield"))
{% endhighlight %}

The Graph type has several other methods that are documented with Scaladoc.


## Programming Task

Your task is to define a `Solution` object that implements the `GraphAlgorithms`
trait. To do so, you'll have to implement several canonical graph algorithms.
In order of difficulty:

- `reachable(graph, start)` returns the set of nodes that are *reachable* from `start`
  by following the edges in `graph`. A node is always reachable from itself, so the
  returned set cannot be empty.

- `isValidPath(graph, path)` returns `true` if `path` is a valid *path* through the graph.
  The empty path is always valid. A path of length 1 is valid only if the node in the path is
  a node in the graph.

- `depthFirstSearch(graph, start, stop)` returns a path from `start` to `stop` that is found
  by a depth-first traversal of the graph. The path is represented as a `List`. The first
  element in the list must be `start` and the last element must be `stop`. The returned
  path must be valid (i.e., `isValidPath`). You should assume that all edges have *weight* 1.
  You may assume that `start` and `stop` are *connected*.

- `breathFirstSearch(graph, start, stop)` returns a path from `start to `stop` that is
  found by a breath-first traversal of the graph. See the description of `depthFirstSearch`
  for additional requirements.

- `shortestPath(graph, start, stop)` returns the total length of the shortest path from `start`
  to `stop`. This function requires all edges to be numbers: the number on an edge is its weight.
  You may assume that `start` and `stop` are *connected*. There are several algorithms for
  calculating the shortest path. Dijkstra's algorithm is the classic. I think the Floyd-Warshall
  algorithm is simpler and more beautiful. There are several others. Implement whichever you like.

You may use the following template to get started:

{% highlight scala %}
import cmpsci220.hw.graph._


object Solution extends GraphAlgorithms {

  def reachable[Node, Edge](graph: Graph[Node, Edge], start: Node): Set[Node] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def isValidPath[Node, Edge](graph: Graph[Node, Edge], path: List[Node]): Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

  def depthFirstSearch[Node, Edge](graph: Graph[Node, Edge], start: Node, stop: Node): Option[List[Node]] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def breathFirstSearch[Node, Edge](graph: Graph[Node, Edge], start: Node, stop: Node): Option[List[Node]] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def shortestPath[Node](graph: Graph[Node, Float], start: Node, stop: Node): Float = {
    throw new UnsupportedOperationException("not implemented")
  }

}
{% endhighlight %}

## Testing BFS and DFS

The key distinction between BFS and DFS is the order in which you visit nodes.
For this assignment, that is the order in which you apply `neighbors`.  You
cannot observe this order from the output of these functions. But,
`Graph.getVisitOrder` can help you do so.

Consider the following graph:

{% highlight scala %}
val g2 = Graph(("A", 1, "B"),
               ("A", 1, "C"),
               ("C", 1, "D"))
{% endhighlight %}

If you search for a path from "A" to "D", both DFS and BFS
will find exactly the same path (there is only one path). But, BFS will
always visit B and C before it visits D:

 - A, B, C, D
 - A, C, B, D

 In contrast, DFS will always visit "D" immediately after "C":

 - A, B, C, D
 - A, C, D (no need to visit B)

You can use the `getVisitOrder` method to return the sequence in
which `neighbors` is invoked. If you make several queries over the same graph
object, use the `resetVisitOrder` method in between each query.

For example, suppose `depthFirstSearch("A", "B")` invokes `neighbors` in the
following sequence:

{% highlight scala %}
g2.neighbors("A")
g2.neighbors("B")
g2.neighbors("C")
g2.neighbors("D")
g2.getVisitOrder() // produces List("A", "B", "C", "D")
{% endhighlight %}

If you invoke `depthFirstSearch("A", "B")` again, you may get the other
sequence:

{% highlight scala %}
// Important! This clears the earlier nodes from the list
g2.resetVisitOrder()

g2.neighbors("A")
g2.neighbors("C")
g2.neighbors("B")
g2.neighbors("D")
g2.getVisitOrder() // produces List("A", "C", "B", "D")
{% endhighlight %}

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload this file to Moodle.