---
layout: lecture
title: Implicits
---

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
