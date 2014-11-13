---
layout: hw
title: "ScalaCheck"
---

<a href="http://xkcd.com/246/">
<img src="http://imgs.xkcd.com/comics/turing_test.png">
</a>

The purpose of this lab is to help you become more familiar with ScalaCheck.
In particular, we're going to play around with generators.

You'll find a lot of useful information in the [ScalaCheck User
Guide](https://github.com/rickynils/scalacheck/wiki/User-Guide). You may also
with to consult the [ScalaCheck
Documentation](http://www.scalacheck.org/documentation.html) for more in-depth
information.

## Preliminaries

### Directory Structure

You should set up a directory that looks like this:

    .scalacheck
    |-- build.sbt
    `-- src
       |-- main
       |   `-- scala
       |       `-- JoinList.scala
       |       `-- Sorting.scala
       `-- test
          `|-- scala
               `-- *your tests go here*

JoinList.scala is available >> [here](JoinList.scala) <<

Sorting.scala is available >> [here](Sorting.scala) <<

### build.sbt File

The `build.sbt` file should have these lines:

{% highlight scala %}
resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)
{% endhighlight %}

### Template File

{% highlight scala %}
import Sorting._
import JoinList._
{% endhighlight %}

## Programming Task

Your task is define generators for small integers, lists of small integers, and
the JoinList type, in addition to writing tests for all of these generators. All
of your code will go in `src/test/scala/`.

You can run your tests using `sbt test` as usual.

*Hint: The tests for the integer lists can be completed using the List class's
methods.*

1. Make a generator called `smallIntegers` that produces integers between 50 and
   150, inclusive.

2. Make a generator called `smallIntLists` that produces lists of integers
   between 50 and 100, inclusive.

3. Make a test that checks that all numbers in a list are less than 200.

4. Make a test that counts how many numbers in a list are less than 100. To
   check the count, filter the list and the check the resulting length.

5. Make a generator for the JoinList[Int] class. You will need to define
   four generators:

    - Empty
    - Singleton
    - JoinList
    - JoinList[JoinList[Int]]
    - You may use the JoinList.fromList function for the last 2 generators.

6. Reimplement the tests from class, but use the JoinList generators that you
   defined above instead.

## Submit Your Work

Upload **only** the test file to Moodle.

**If you work with a partner:** Both of you should submit the file. In addition,
**put a comment with both of your names at the top of the file**.

For example, at the top of ScalaCheckLabTests.scala:

// Completed by: Sarah Scalington and Jack Javahead
