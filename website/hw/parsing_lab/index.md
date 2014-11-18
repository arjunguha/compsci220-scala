---
layout: hw
title: "ParsingLab"
---

The purpose of this lab is to help you get you started with your parsing homework.

## Preliminaries

### Directory Structure

You should set up a directory that looks like this:

    .scalacheck
    |-- build.sbt
    `-- src
       |-- main
       |   `-- scala
       |       `-- ParsingLab.scala
       `-- test
          `|-- scala
               `-- *your tests go here*


### build.sbt File

The `build.sbt` file should have these lines:

{% highlight scala %}
name := "parsing"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.10"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
{% endhighlight %}

### Template File

{% highlight scala %}
import scala.util.parsing.combinator._

object ArithParser extends RegexParsers with PackratParsers {

  lazy val digit: PackratParser[_] = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
  lazy val number: PackratParser[_] 
  lazy val atom: PackratParser[_] 
  lazy val exponent: PackratParser[_] 
  lazy val add: PackratParser[_] 
  lazy val mul: PackratParser[_] 
  lazy val expr: PackratParser[_]

}
{% endhighlight %}

## Programming Task


You will be implementing the grammar to parse arithmetic expression. The grammar to 
implement the parse string is given in the link below.

https://people.cs.umass.edu/~arjun/courses/cmpsci220-fall2014/hw/parsing/

On providing a correct arithmetic expression, canParse should return true. 

An incorrect expression should return false.

### Check Your Work

{% highlight scala %}
class TestSuite extends org.scalatest.FunSuite {

  def canParse(str: String): Boolean = {
    !ArithParser.parseAll(ArithParser.expr, str).isEmpty
  }

  test("test 2") {
	assert(canParse("10 + 4"))
  }

}

{% endhighlight %}


## Submit Your Work

Upload **only** ParsingLab.scala in Moodle.

**If you work with a partner:** Both of you should submit the file. In addition,
**put a comment with both of your names at the top of the file**. In case, 



