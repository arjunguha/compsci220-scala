---
layout: hw
title: Parsing
---

For this assignment, you will write a parser, printer, and evaluator for arithmetic
expressions.  To do so, you will (1) learn how to read a *BNF grammar*, (2)
learn how to use Scala's parser-combinator library, and (3) use property-based
testing, using Scalatest.

## Preliminaries

The support code for this assignment is in the `cmpsci220.hw.parsing` package.

You should create a series of directories that look like this:

<pre>
 ./parsing
 |-- build.sbt
 `-- project
     `-- plugins.sbt
 `-- src
     |-- main
     |   `-- scala
     |       `-- <i>your solution goes here</i>
     `-- test
        `|-- scala
             `-- <i>your tests goes here</i>
</pre>

Your `build.sbt` file must have exactly these lines:

{% highlight scala %}
name := "parsing"

scalaVersion := "2.11.2"

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
{% endhighlight %}

The `plugins.sbt` file must have exactly this line:

{% highlight scala %}
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "2.2")
{% endhighlight %}

## Overview

For this assignment, you will work with a language of arithmetic expressions:
numbers, addition, subtraction, multiplication, and division. Here are some
examples of the *concrete syntax* of the language:

    - `1 + 2`
    - `10`
    - `2 * 3 + 5 * -10`
    - `2 * (3 + 5) * -10`

    - `2 * (3 + 5) ^ 2 * -10`

This grammar specifies the syntax of the language:

    number ::= -? [0-9]+ (. [0-9]+)?

    atom ::= number
           | ( expr )

    exp ::= atom
          | exp ^ atom

    add ::= exp
          | exp + add
          | exp - add

    mul ::= add
          | add * mul
          | add / mul

    expr ::= mul

Your first task is to implement a parser that parses strings to the `Expr`
type. For example, `parse("1 + 2")` parses to `Add(Num(1), Num(2))`.
To do so, you will use Scala's parser combinator library with Packrat parsing.

Your second task is to implement a printer, which returns strings that represent
arithmetic expressions. An important property of the printer is its relationship
with the parser:

`parseExpr(print(e)) == e`, for all expressions `e`

It is tedious to write test cases for this property, since there are so many
different kinds of expressions. Instead, we use use *ScalaCheck* to test this
property on randomly generated expressions.

Finally, for completeness, you'll write an evaluator for arithmetic expressions.

## Programming Task

You can use this template for your solution:

{% highlight scala %}
import cmpsci220.hw.parsing._
import scala.util.parsing.combinator._

object ArithEval extends ArithEvalLike {
  def eval(e: Expr): Double = {
    throw new UnsupportedOperationException("not implemented")
  }
}

object ArithParser extends ArithParserLike {

  lazy val atom: PackratParser[Expr] = {
    throw new UnsupportedOperationException("not implemented")
  }

  lazy val exp: PackratParser[Expr] = {
    throw new UnsupportedOperationException("not implemented")
  }

  lazy val add: PackratParser[Expr] = {
    throw new UnsupportedOperationException("not implemented")
  }

  lazy val mul: PackratParser[Expr] = {
    throw new UnsupportedOperationException("not implemented")
  }

  lazy val expr: PackratParser[Expr] = {
    throw new UnsupportedOperationException("not implemented")
  }
}

object ArithPrinter extends ArithPrinterLike {
  def print(e: Expr): String = {
    throw new UnsupportedOperationException("not implemented")
  }
}

{% endhighlight %}

We suggest proceeding in the following order:

1. Implement `ArithEval`. This is a simple recursive function.
2. Implement `ArithParser` by translating the grammar provided above to Scala's
   parser combinators, as discussed in the Odersky book.
3. Implement `ArithPrinter`.

You can and should write individual test cases. But, you can use this ScalaCheck
property to test parsing and printing in tandem:

{% highlight scala %}
test("parse and pretty are related") {
  forAll(genExpr) { (e: Expr) =>
    assert(parseArith(print(e)) == e)
  }
}
{% endhighlight %}

## Check Your Work

Here is a trivial test suite that simply checks to see if you've defined
the `Solution` object with the right type:

{% highlight scala %}
class TrivialTestSuite extends org.scalatest.FunSuite {

  test("several objects must be defined") {
    val parser: cmpsci220.hw.parsing.ArithParserLike = ArithParser
    val printer: cmpsci220.hw.parsing.ArithPrinterLike = ArithPrinter
    val eval: cmpsci220.hw.parsing.ArithEvalLike = ArithEval
  }


}
{% endhighlight %}

You should place this test suite in `src/test/scala/TrivialTestSuite.scala`.
If this test suite does not run as-is, you risk getting a zero.

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.
