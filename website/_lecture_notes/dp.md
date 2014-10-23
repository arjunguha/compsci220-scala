---
layout: lecture
title: Dynamic Programming
---

** Code in lecture-code/  but has been edited below. **

This is not the greatest lecture. Students should have more exposure to
backtracking before they see this material.

## Preliminaries

{% highlight scala %}
"hello world".toList
"hello world".toArrjun
{% endhighlight %}

## Backtracking substring

{% highlight scala %}
def substring[A](sublst: List[A], lst: List[A]): Boolean = {
  (sublst, lst) match {
    case (Nil, _) => true
    case (x :: sublst1, y :: lst1) => {
      if (x == y) {
        listContains(sublst1, lst1) || listContains(x :: sublst1, lst1)
      }
      else {
        listContains(x :: sublst1, lst1)
      }
    }
    case (_ :: _, Nil) => false
  }
}
{% endhighlight %}

We may recur into `lst1` *twice* at each element in the list.

{% highlight scala %}
substring("abc".toList, "abcdef".toList) // no backtracking
substring("abc".toList, "aabcdef".toList) // backtracks once
substring("aab".toList, "abababababaab".toList) // backtracks repeatedly
{% endhighlight %}

## Subsequence (no backtracking)

{% highlight scala %}
def subsequence[A](xs: List[A], ys: List[A]): Boolean = (xs, ys) match {
  case (Nil, _) => true
  case (_, Nil) => false
  case (x::xs1, y::ys1) => {
    if (x == y) {
      subsequence(xs1, ys1)
    }
    else {
      subsequence(x::xs1, ys1)
    }
  }
}

substring("jun Gu", "Arjun Guha") // true
substring("juju", "Arjun Guha") // false
{% endhighlight %}

## Common subsequence

{% highlight scala %}
def commonSubsequence[A](xs: List[A], ys: List[A], zs: List[A]): Boolean = {
  subsequence(xs, ys) && subsequence(xs, zs)
}
{% endhighlight %}

## Finding the Longest Common Subsequence

{% highlight scala %}
var counter = 0
def lcs[A](xs: List[A], ys: List[A]): List[A] = {
  counter = counter + 1
  (xs, ys) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (x :: xs1, y :: ys1) => {
      if (x == y) {
        x :: lcs(xs1, ys1)
      }
      else {
        val tmp1 = lcs(xs, ys1)
        val tmp2 = lcs(xs1, ys)
        if (tmp1.length > tmp2.length) {
          tmp1
        }
        else {
          tmp2
        }
      }
    }
  }
}
{% endhighlight %}

We *always* recur twice, when the heads are not equal.

**If you add `println` to see the recursive calls, you'll see that we are
repeatedly recurring on the same substrings.**

## Recursive LCS-Length with an array

Let's turn these into arrays. We can print the indices, which make it
easier to see the repeated calls:

{% highlight scala %}
def lcsLen[A](xs: Array[A], ys: Array[A]): Int = {
  var counter = 0

  def f[A](i: Int, j: Int): Int = {
    println(i, j)
    counter = counter + 1
    if (i == 0 || j == 0) {
      if (xs(i) == ys(j)) { 1 } else { 0 }
    }
    else if (xs(i) == ys(j)) {
      1 + f(i - 1, j - 1)
    }
    else {
      val tmp1 = f(i, j - 1)
      val tmp2 = f(i - 1, j)
      if (tmp1 > tmp2) {
        tmp1
      }
      else {
        tmp2
      }
    }
  }

  val result = f(xs.length - 1, ys.length - 1)
  println(s"Took $counter steps")
  result
}
{% endhighlight %}

## Dynamic Programming LCS Length

{% highlight scala %}
def lcsLen[A](xs: Array[A], ys: Array[A]): Int = {
  val table = Array.fill(xs.length) { Array.fill(ys.length) { 0 } }
  var counter = 0

  def f[A](i: Int, j: Int): Int = {
    counter = counter + 1
    println(i, j)
    if (i == 0 || j == 0) {
      if (xs(i) == ys(j)) { 1 } else { 0 }
    }
    else if (xs(i) == ys(j)) {
      1 + table(i - 1)(j - 1)
    }
    else {
      val tmp1 = table(i)(j - 1)
      val tmp2 = table(i - 1)(j)
      if (tmp1 > tmp2) {
        tmp1
      }
      else {
        tmp2
      }
    }
  }

  for (i <- 0.to(xs.length - 1)) {
    for (j <- 0.to(ys.length - 1)) {
      table(i)(j) = f(i, j)
    }
  }

  println(s"Took $counter steps")
  table(xs.length - 1)(ys.length - 1)
}
{% endhighlight %}
