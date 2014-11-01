---
layout: lecture
title: Implicits
---

## Motivation: DSLs

Regex a built-in DSL.

## Operators as methods

- You can write `def +`, `def *`, etc.
- This can be very confusing, so I recommend `<+>`, `<*>`, etc.
- You don't have control over precedence

## Implicit classes

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
}
{% endhighlight %}

## Implicit functions

Type conversion example

{% highlight scala %}
object Dsl {

  ...

  implicit def charToRegex(ch: Char): Regex = Character(ch)

  implicit def stringToRegex(str: String): Regex = {
    str.toList.foldRight(One : Regex) { (ch, re) => Seq(Character(ch), re) }
  }
}
{% endhighlight %}

## The Process DSL

- http://www.scala-lang.org/api/current/index.html#scala.sys.process.package

- As opposed to:

http://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html#start()

## The Duration DSL

http://www.scala-lang.org/api/current/index.html#scala.concurrent.duration.Duration

## String Ops

http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.StringOps

