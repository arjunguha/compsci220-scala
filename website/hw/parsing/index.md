---
layout: hw
title: Parsing
---

<a href="http://xkcd.com/1171/"><img src="http://imgs.xkcd.com/comics/perl_problems.png"></a>

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

libraryDependencies += "edu.umass.cs" %% "cmpsci220" % "1.10"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
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

        mul ::= exp | exp * mul | exp / mul
        add ::= mul | mul + add | mul - add
        expr :: add


    number ::= -? [0-9]+ (. [0-9]+)?

    atom ::= number
           | "(" expr ")"

    exponent ::= atom
               | exponent "^" atom

    mul ::= exponent
          | exponent "*" mul
          | exponent "/" mul

    add ::= mul
          | mul "+" add
          | mul "-" add

    expr ::= add

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

  // number: PackratParser[Double] is defined in ArithParserLike

  lazy val atom: PackratParser[Expr] = {
    throw new UnsupportedOperationException("not implemented")
  }

  lazy val exponent: PackratParser[Expr] = {
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

The `build.sbt` file includes ScalaCheck, which you're free to use in testing.
(You'll have to define generators as part of the test suite.)

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
The tests must pass with no changes to the file.

## <a name="review"></a>Testing and Review

For this assignment there will be two testing and review phases worth 5%
of your grade each (i.e., 10% total).

**Submission Due: Nov 20, 23:59**
**Reviews Due: Nov 22, 23:59**

Create a test suite called `GradedParserTestSuite.scala`. In it, place 5--10
interesting test cases for the `ArithParser` and `ArithPrinter` object methods.
Write tests that try to cover the space of interesting inputs to these
methods.

### Submitting to Captain Teach

You can login to Captain Teach using your `@umass.edu` e-mail address. To
ensure you are logged into this account, first visit:

[https://apps.umass.edu/](https://apps.umass.edu/)

Once you have authenticated your account visit:

[https://www.captain-teach.org/umass-cmpsci220/assignments/](https://www.captain-teach.org/umass-cmpsci220/assignments/)

Submit your `GradedParserTestSuite.scala` to the `parsing` assignment.

After uploading your work, you will be given an opportunity to preview and
change it. Once you are satisfied with your submission, be sure to click
the `Publish` button for your assignment at the bottom of the screen.

After submitting, you will be assigned to one of three groups:

  * Reviewer - You will complete reviews for other students but you will
    not receive any reviews.
  * Reviewee - You will receive reviews from other students but you will
    not complete any reviews.
  * No Reviews - You will neither recieve nor complete reviews from other
    students

### Final Submission and Survey

After you have completed your assignment, you will need to return to Captain
Teach to turn in your final solution. This is the same file you upload to 
moodle. You will not be able to submit this to Captain Teach if you have
pending reviews to complete. After submitting your final implementation,
you will be given a survey based on the group you were assigned for reviews.
Complete this survey and submit it.

## Submit Your Work

Use the `submit` command within `sbt` to create `submission.tar.gz`. Upload
this file to Moodle.
