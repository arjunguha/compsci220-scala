---
layout: lecture
title: Brzozowski Derivatives
---

This lecture assumes students know how to match regular expressions by
backtracking search. Brzozowski derivatives will give them a deeper
understanding of regular expressions and is yet another exercise in programming
recursive functions and data transformations.

## Regular Expression Optimization

Also know as algebra.

Make these smaller:

- `(ab)|(ac)`
- `a|a`
- `(ba)|(ca)`
- `(a*)|(a)`

## Smart Constructors

{% highlight scala %}
def alt(re1: Regex, re2: Regex): Regex = (re1, re2) match {
  case (Zero, q) => q
  case (p, Zero) => p
  case (p, q) if (p == q) => p
  case _ => Alt(re1, re2)
}

def seq(re1: Regex, re2: Regex): Regex = (re1, re2) match {
  case (Zero, q) => Zero
  case (p, Zero) => Zero
  case (One, q) => q
  case (p, One) => p
  case _ => Seq(re1, re2)
}
{% endhighlight %}

## Contains Empty?

Does this regular expression accept the empty string?

- `"abc"`
- `a*`
- `a|(b*)`
- `(a*)(b*)(c*)`
- `(a*)((b|c)*)(d***)`

{% highlight scala %}
def containsEmpty(re: Regex): Regex = re match {
  case One => true
  case Zero => false
  case Character(_) => false
  case Alt(lhs, rhs) => containsEmpty(lhs) || containsEmpty(rhs)
  case Seq(lhs, rhs) => containsEmpty(lhs) && containsEmpty(rhs)
  case Star(_) => true
}
{% endhighlight %}

## Derivative examples

Support `s` is a string beginning with the character `a`.  Could `s`
match any of these regular expressions:

- `a`
- `b`
- `abc`
- `ab*
- `b|a`
- `b*a`
- `(aaa)|(bbb)

Assuming that `s` does match the regex, give a regular expression to match
the rest of `s`:

- 1
- does not match
- `bc`
- `b*`
- 1
- 1
- `aa`

Consider examples of strings beginning with `a`

{% highlight scala %}
def next(char: Char, re: Regex): Regex = re match {
  case One => Zero
  case Zero => Zero
  case Character(ch) => if (char == ch) One else Zero
  case Alt(lhs, rhs) => alt(next(char, lhs), next(char, rhs))
  case Seq(lhs, rhs) =>
    if (containsEmpty(lhs)) {
      alt(seq(next(char, lhs), rhs), next(char, rhs))
    else {
      seq(next(char, lhs), rhs)
      }
    }
  case Star(re) => seq(next(char, re), Star(re))
}
{% endhighlight %}

## Memoization

Memoization abstraction:

{% highlight scala %}
class Memoize[A,B](f: A => B) {

  val tbl = collection.mutable.Map[A,B]()

  def apply(x: A): B = {
    tbl.get(x) match {
      case None => {
        val y = f(x)
        tbl += (x -> f(x))
        y
      }
      case Some(y) => y
    }
  }
}
{% endhighlight %}

Refactor `next` to use a memoized next instead of `next` directly.
