---
layout: hw
title: "MST : Kruskal's Algorithm"
---

<a href="http://xkcd.com/246/">
<img src="http://imgs.xkcd.com/comics/labyrinth_puzzle.png">
</a>


This assignment has three main objectives:

- Implement Minimum Spanning Tree
- Use Kruskal's Algorithm
- You will use Scala's built-in collection classes. These are
  discussed in the Odersky textbook and there is a lot of information about
  them on the Web.

Feel free to use the web for understanding the algorithms. In fact, I encourage
you to find pseudocode on the Web and translate it to Scala.

## Preliminaries

You should create a series of directories that look like this:

    ./mst
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
scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

scalacOptions += "-feature"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

The `plugins.sbt` file should have exactly this line:

{% highlight scala %}
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "2.2")
{% endhighlight %}

Finally, here is `TrivialTestSuite.scala`:

{% highlight scala %}

import cmpsci220.hw.graph._
import MST._

class MSTTestSuite extends org.scalatest.FunSuite {

  test("MST test1") {
    val g = Graph(("A", 10, "B"),
      ("B", 20, "C"),
      ("A", 50, "C"))
    val t = mst(g)

    for (ns <- t.nodes.tails.filter(_.length >= 2)) {
      val n1 = ns.head
      for (n2 <- ns.tail) {
        if (t.neighbors(n1).contains(n2)) {
          println(s"$n1 -- ${t.getEdge(n1, n2)} -- $n2")
        }
      }
    }
  }

  test("MST test2") {
    val g = Graph(("A", 10, "B"),
      ("B", 20, "C"),
      ("A", 50, "C"),
      ("A", 6, "D"),
      ("C", 7, "D"))
    val t = mst(g)

    for (ns <- t.nodes.tails.filter(_.length >= 2)) {
      val n1 = ns.head
      for (n2 <- ns.tail) {
        if (t.neighbors(n1).contains(n2)) {
          println(s"$n1 -- ${t.getEdge(n1, n2)} -- $n2")
        }
      }
    }
  }

  test("MST test3") {
    val g = Graph(("A", 4, "B"),
      ("B", 3, "C"),
      ("C", 2, "D"),
      ("D", 1, "E"),
      ("E", 1, "F"),
      ("F", 5, "A"),
      ("A", 3, "D"),
      ("B", 4, "D"))
    val t = mst(g)

    for (ns <- t.nodes.tails.filter(_.length >= 2)) {
      val n1 = ns.head
      for (n2 <- ns.tail) {
        if (t.neighbors(n1).contains(n2)) {
          println(s"$n1 -- ${t.getEdge(n1, n2)} -- $n2")
        }
      }
    }
  }

  test("MST test4") {
    val g = Graph(("A", 10, "B"),
      ("B", 20, "C"),
      ("A", 50, "C"),
      ("A", 6, "D"),
      ("C", 7, "D"))
    val t = mst(g)

    for (ns <- t.nodes.tails.filter(_.length >= 2)) {
      val n1 = ns.head
      for (n2 <- ns.tail) {
        if (t.neighbors(n1).contains(n2)) {
          println(s"$n1 -- ${t.getEdge(n1, n2)} -- $n2")
        }
      }
    }
  }

  test("MST test5") {
    val g = Graph(("A", 6, "B"),
      ("B", 5, "C"),
      ("C", 1, "A"),
      ("C", 5, "D"),
      ("C", 6, "E"),
      ("C", 4, "F"),
      ("D", 2, "F"),
      ("E", 6, "F"),
      ("A", 5, "D"),
      ("B", 3, "E"))
    val t = mst(g)

    for (ns <- t.nodes.tails.filter(_.length >= 2)) {
      val n1 = ns.head
      for (n2 <- ns.tail) {
        if (t.neighbors(n1).contains(n2)) {
          println(s"$n1 -- ${t.getEdge(n1, n2)} -- $n2")
        }
      }
    }
  }
}

{% endhighlight %}


## The Graph Abstract Data Type

For graph structure and methods refer the graph assignment homework.

## Programming Task

Your task is to define a `Solution` object that implements Minimum Spanning Tree using Kruskal`s
algorithm.

You may use the following template to get started:

{% highlight scala %}
import cmpsci220.hw.graph._
import cmpsci220.hw.disjointset._

object MST {

// Create a list of tuples. Each tuple comprises of vertex1, weight of the joining edge and vertex2
  def allEdges[V](graph: Graph[V, Int]): List[(V, Int, V)]= {

  }

// Implement mst using Kruskal's algorithm
  def mst[V](graph: Graph[V,Int]): Graph[V,Int]= {

  }
}
{% endhighlight %}

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload this file to Moodle.