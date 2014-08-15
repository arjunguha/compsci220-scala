---
layout: lecture
title: Pattern Matching
---

Imagine you're a new age librarian, tasked with cataloging information on the
Internet. There are many types of information. Here are some significant ones:

~~~ scala
case class Tweet(user: String, number: Long)
case class Xkcd(number: Int)
case class HackerNews(item: Int, points: Int)
~~~

Here are some examples:

~~~ scala
// https://twitter.com/PLT_Borat/status/248038616654299136
val tweet = Tweet("PLT_Borat", 248038616654299136L)
// http://xkcd.com/1316/
val comic = Xkcd(1316)
// https://news.ycombinator.com/item?id=8169367
val news = HackerNews(8169367, 305)
~~~

Let's write a function called `getURL` that maps these items to their URLs.
Here is how do it in Scala:

~~~ scala
def getURL(item: Any): String = item match {
  case Tweet(user, number) => "https://twitter.com/" + user + "/status/" + number
  case Xkcd(number) => "http://xkcd.com/" + number
  case HackerNews(item, _) => "http://news.ycombinator.com/item?id=" + item
  case _ => error("not a library item")
}
~~~

This definition is unsatisfactory. `getURL` takes values of `Any` type. So,
it is really easy to get a runtime error:

~~~
scala> getURL("hello")
java.lang.RuntimeException: not a library item
 at scala.sys.package$.error(package.scala:27)
  at cmpsci220.package$$anonfun$1.apply(package.scala:13)
  at cmpsci220.package$$anonfun$1.apply(package.scala:13)
  at .getURL(<console>:38)
  ... 33 elided
~~~

To eliminate this kind of error, we need an `Item` type:

~~~ scala
sealed trait Item
case class Tweet(user: String, number: Long) extends Item
case class Xkcd(number: Int) extends Item
case class HackerNews(item: Int, points: Int) extends Item
~~~

A *trait* in Scala is like an *interface*. They're also much more versatile
than interfaces, but we'll get into that later.

Now, we can rewrite `getURL`, restricting argument type:

~~~ scala
def getURL(item: Item): String = item match {
  case Tweet(user, number) => "https://twitter.com/" + user + "/status/" + number
  case Xkcd(number) => "http://xkcd.com/" + number
  case HackerNews(item, _) => "http://news.ycombinator.com/item?id=" + item
}
~~~

This time, applying `getURL` to a non-item produces a type error as expected:

~~~
scala> item("hello")
<console>:43: error: type mismatch;
 found   : String("hello")
 required: Item
              getURL("hello")
~~~

A really nice feature of `match` is that it checks to ensure you've handled all
cases. For example, suppose we forgot to write the HackerNews case. Scala
prints the following error:

~~~
<console>:18: warning: match may not be exhaustive.
It would fail on the following input: HackerNews(_, _)
       def getURL(item: Item): String = item match {
                                        ^
error: No warnings can be incurred under -Xfatal-warnings.
~~~

### Matching Values

You can use `match` to also match concrete values. For example, here is
variant of getURL that censors a particular tweet from PLT Borat:

~~~ scala
def getURL(item: Item): String = item match {
  case Tweet("PLT_Borat", 301113983723794432L) => "http://disney.com"
  case Tweet(user, number) => "https://twitter.com/" + user + "/status/" + number
  case Xkcd(number) => "http://xkcd.com/" + number
  case HackerNews(item, _) => "http://news.ycombinator.com/item?id=" + item
}
~~~

Imagine you'd added the censoring line, but accidentally removed the line
that handles all other Tweets. Again, Scala will catch the error:

~~~
<console>:62: warning: match may not be exhaustive.
It would fail on the following inputs:
  Tweet("PLT_Borat", (x: Long forSome x not in 301113983723794432L)),
  Tweet((x: String forSome x not in "PLT_Borat"), 301113983723794432L),
  Tweet((x: String forSome x not in "PLT_Borat"), _),
  Tweet(_, (x: Long forSome x not in 301113983723794432L))
       def getURL(item: Item): String = item match {
                                        ^
error: No warnings can be incurred under -Xfatal-warnings.
~~~

It is a long error message. But, if you read it carefully, you'll see that it is
very precisely describing exactly the cases that are missing.

## Additional Reading

Read Chapter 15, "Case Classes and Pattern Matching" from *Programming in Scala*.
**You can stop after reading Section 15.5.**


{% include links.md %}