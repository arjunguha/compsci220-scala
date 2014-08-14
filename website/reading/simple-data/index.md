---
layout: lecture
title: Simple Data Structures
---

Just like Java, Scala has classes and even supports advanced features that are
beyond the scope of classes in Java. Scala classes tend to be shorter than their
Java counterparts. For example, here is a simple Java class that represents a
point:

~~~ java
public class Point {

  double x;
  double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

}

Point pt = new Point(1, 2)
~~~

Here is the Scala equivalent:

~~~ scala
class Point(x : Double, y : Double)
val pt = new Point(1, 2)
~~~

Naturally, if want to add methods or other constructors, you'll have to write
more code in Scala too. But, simple classes tend to be very short.

But, we are not going to use classes yet. Instead, we are going to use *case
classes*, which are unique to Scala.

## Case Classes

If you write **case class** instead of just class, you get several conveniences:

~~~ scala
case class Point(x: Double, y: Double)
~~~

First, you can create values without writing `new`:

~~~ scala
val pt = Point(1, 2)
~~~

Second, case classes have an automatically generated `toString` method that
prints the fields:

~~~
scala> pt
pt: Point = Point(1.0,2.0)
~~~

Third, all fields are public by default, so you can easily write simple
functions, such as the this one, without writing getters:

~~~ scala
def magnitude(pt: Point) : Double = {
  math.sqrt(pt.x * pt.x + pt.y * pt.y)
}

test("3-4-5 triangles") {
  assert(magnitude(Point(3, 4)) == 5)
}
~~~

## Several Cases

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



{% include links.md %}