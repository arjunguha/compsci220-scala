---
layout: lecture
title: ScalaCheck
---

## Preliminaries

{% highlight scala %}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck._

class MyTestSuite extends FunSuite with GeneratorDrivenPropertyChecks {
{% endhighlight %}

Sometimes, writing individual test cases isn't enough or isn't desirable.
It can help to think of properties instead of test cases.

## Properties of list processing functions

{% highlight scala %}
test("concat distributes over map") {
  def incr(x: Int) = x + 1

  forAll { (lst1: List[Int], lst2: List[Int]) =>
    assert((lst1 ++ lst2).map(incr) == lst1.map(incr) ++ lst2.map(incr))
  }

}

test("reverse(reverse(lst))") {
  forAll { (lst: List[Int]) =>
    assert(lst.reverse.reverse == lst)
  }
}
{% endhighlight %}

## Optimized Fibonacci

Make fibonacci faster using a loop:

{% highlight scala %}
def fib(n: Int): Int = {
  if (n == 0) {
    1
  }
  else if (n == 1) {
    1
  }
  else {
    fib(n - 1) + fib(n - 2)
  }
}

def fibLoop(n: Int): Int = {
  if (n <= 1) {
    return 1
  }
  var fib_n_minus_2 = 1
  var fib_n_minus_1 = 1
  var fib_n = fib_n_minus_2  + fib_n_minus_1
  for (i <- 2.until(n)) {
    fib_n_minus_1 = fib_n_minus_2
    fib_n_minus_2 = fib_n
    fib_n = fib_n_minus_2  + fib_n_minus_1
  }
  return fib_n
}
{% endhighlight %}

This doesn't work, since it picks a large int that makes recursive `fib`
blow the stack:

{% highlight scala %}
test("fibonacci optimization (causes stackoverflow usually)") {
  forAll { n =>
    assert(fibLoop(n) == fib(n))
  }
}
{% endhighlight %}

Try to figure out the largest input that `fib` can handle:

{% highlight scala %}
test("fibonacci optimization") {

  forAll(Gen.choose(0, 20)) { n =>
    assert(fibLoop(n) == fib(n))
  }

}
{% endhighlight %}

## Generating Data Structures (Regular Expressions)

Generating regular expressions:

{% highlight scala %}
object GenRegex {

  private def genChar: Gen[Regex] = for {
    ch <- Gen.oneOf('a', 'b', 'c', 'd', 'e', 'f')
  } yield Character(ch)

  private def genStar(size: Int): Gen[Regex] = for {
    p <- genSizedRegex(size - 1)
  } yield Star(p)

  private def genAlt(size: Int): Gen[Regex] = for {
    p <- genSizedRegex(size / 2)
    q <- genSizedRegex(size / 2)
  } yield Alt(p, q)

  private def genSeq(size: Int): Gen[Regex] = for {
    p <- genSizedRegex(size / 2)
    q <- genSizedRegex(size / 2)
  } yield Seq(p, q)


  private def genSizedRegex(size: Int): Gen[Regex] = {
    if (size == 0) {
      genChar
    }
    else {
      Gen.oneOf(genSeq(size), genAlt(size), genStar(size))
    }
  }

  def genRegex = Gen.sized(genSizedRegex)

}
{% endhighlight %}

Now, we can write a real test for the regular expression matcher:

{% highlight scala %}
test("testing naive matching") {
  forAll(GenRegex.genRegex) { (re: Regex) =>
    for (w <- Words.words(re).take(10)) {
      println(w)
      assert(Regex.matches(re, w), s"${w.mkString} should be in $re")
    }
  }
}
{% endhighlight %}

This exposes a bug in the naive matcher!



