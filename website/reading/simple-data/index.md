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

## Additional Reading

Read Chapter 5, "Basic Types and Operations" from *Programming in Scala*.

{% include links.md %}