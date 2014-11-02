---
layout: lecture
title: Implicits
---

This lecture introduces implicits. To motiviate implicits, we discuss (1)
embedded DSLs and (2) extended existing APIs. We also discuss how to
discuss operators as methods, though students have seen this before.

## What's wrong with vanilla APIs?

Consider a type for 2D vectors:

{% highlight scala %}
class Vector2D(val x: Double, val y: Double) {

  def add(other: Vector2D): Vector2D = new Vector2D(this.x + other.x, this.y + other.y)

  def mul(factor: Double): Vector2D = new Vector2D(factor * x, factor * y)

  def neg = Vector2D(-x, -y)

}
{% endhighlight scala %}

This type is cumbersome to use:

{% highlight scala %}
val v1 = new Vector2D(2, 3)
val v2 = v1.add(v1) * 3
{% endhighlight %}

A mathematician would rather write:

{% highlight scala %}
val v1 = (2, 3)
val v2 = (v1 + v1) * 3
{% endhighlight %}

We'll see how to do this.

## Operators as methods

Scala lets us define operators as methods, so refactor to the following:

{% highlight scala %}

import scala.language.implicitConversions

class Vector2D(val x: Double, val y: Double) {

  def +(other: Vector2D): Vector2D = new Vector2D(this.x + other.x, this.y + other.y)

  def *(factor: Double): Vector2D = new Vector2D(factor * x, factor * y)

  def unary_- = new Vector2D(-x, -y)

}
{% endhighlight %}

Now, we can write:

{% highlight scala %}
val v1 = new Vector(2, 3)
val v2 = (v1 + v1) * 3
{% endhighlight %}

This is the same as:

{% highlight scala %}
val v1 = new Vector(2, 3)
val v2 = v1.+(v1).*(3)
{% endhighlight %}

We still have to write `new Vector`. We still can't write:

{% highlight scala %}
val v2 = 3 * v1
{% endhighlight %}

Since it is the same as `3.*(v1)` (i.e., it requires a `*` method on `Int`
that takes a `Vector2D`.)

## Implicit classes

In principle, we want to add a `*(Vector2D): Vector2D` method to `Int`, but we
can't, since `Int` is a builtin class. Instead, we can create a wrapper object:

{% highlight scala %}
class RichInt(n: Int) {
  def *(v: Vector2D): Vector2D = v * n
}
val v1 = new Vector(2, 3)
val v2 = (new RichInt(3)) * (v1 + v1)
{% endhighlight %}

We solved our problem, but introduced a new one: writing `new RichInt` is
tedious and unnatural.

We can use implicit classes:

{% highlight scala %}
object Implicits {

  implicit class RichInt(n: Int) {
    def *(v: Vector2D): Vector2D = v * n
  }

}

import Implicits._

val v1 = new Vector(2, 3)
val v2 = 3 * (v1 + v1)
{% endhighlight %}

Scala's type-checker rewrites `3 * ...` to `(new RichInt(3)) * ...`.
Notably, we didn't have to make any changes to `Int` (not that we could).

In fact, suppose the `Vector2D` class were builtin and didn't have these
methods. (The one I provided did not have a multiplication method.)

{% highlight scala %}
implicit class RichVector2D(vec: Vector2D) {
  import vec._
  def +(other: Vector2D): Vector2D = new Vector2D(x + other.x, y + other.y)
  def *(factor: Double): Vector2D = new Vector2D(factor * x, factor * y)
  def unary_- = new Vector2D(-x, -y)
}
{% endhighlight %}

**This idiom is impossible to do without static types.**

The `import Implicits._` is necessary, otherwise it doesn't know to use the
methods in `RichInt`. There are many, many builtin implicits:

{% highlight scala %}
scala> :implicits -v
{% endhighlight %}

## Implicit Functions

We are still writing this:

{% highlight scala %}
val v1 = new Vector(2, 3)
{% endhighlight %}

But, we aren't calling a method here, so we can't use implicit classes to
solve this.

{% highlight scala %}
object Implicits {

  implicit def pointToVector2D(pt: (Int, Int)): Vector2D = {
    val (x, y) = pt
    new Vector2D(x, y)
  }

  ...

}

val v1: Vector2D = (2, 3)
val v2 = 3 * (v1 + v1)
{% endhighlight %}

This turns into:

{% highlight scala %}
val v1: Vector2D = pointToVector2D((2, 3))
val v2 = (new RichInt(3)) * (v1 + v1)
{% endhighlight %}

## Some builtin DSLs that use implicits

- The [Process DSL]as opposed to the Java [ProcessBuilder].
- The [Duration DSL]


## A Regex DSL

**Note:** This assumes students have already covered regular expressions.

We could have added methods to `Regex`, but imagine that it is an existing
package that we can't modify. In the code below we also add methods to
`String` and `Char`, which are built-in to Scala.

{% highlight scala %}
import scala.language.{implicitConversions, postfixOps}

object Dsl {

  implicit class RichRegex(regex: Regex) {
    def <||>(other: Regex): Regex = Alt(regex, other)
    def <*>(): Regex = Star(regex)
  }

  implicit class RichChar(ch: Char) {
    def <||>(other: Regex) = Alt(Character(ch), other)
  }

  implicit class RichString(str: String) {
    def <||>(other: Regex) = Alt(stringToRegex(str), other)
    def <*>(): Regex = Star(stringToRegex(str))
  }

  implicit def charToRegex(ch: Char): Regex = Character(ch)

  implicit def stringToRegex(str: String): Regex = {
    str.toList.foldRight(One : Regex) { (ch, re) => Seq(Character(ch), re) }
  }

}
{% endhighlight %}

[ProcessBuilder]: http://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html#start()
[Process DSL]: http://www.scala-lang.org/api/current/index.html#scala.sys.process.package
[Duration DSL]: http://www.scala-lang.org/api/current/index.html#scala.concurrent.duration.Duration