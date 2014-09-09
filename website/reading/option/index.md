---
layout: lecture
title: The Option Type
---

## Partial Functions and Signalling Errors

Many functions are not defined on all inputs. For example, if you're reading
input from a keyboard (i.e., a string) and want to parse the string as a
number, you can apply `Integer.parseInt`.

    scala> val n = Integer.parseInt("10")
    n: Int = 10

But, if the string is not a numeral, you get an exception:

    scala> val n = Integer.parseInt("ten")
    java.lang.NumberFormatException: For input string: "ten"

You've encountered other ways of signalling errors. For example, if you lookup
an unbound key in a hashtable, Java (and Scala) produce `null`:

    > val ht = new java.util.Hashtable[Int, String]()
    ht: java.util.Hashtable[Int,String] = {}
    scala> ht.put(10, "hello")
    scala> val r = ht.get(20)
    r: String = null

Finally, here is a more insidious example. The following function calculates the
point where two lines, defined by *y = m1.x + b* and *y = m2.x + b2*, intersect.
The function is not defined when the two lines are parallel (i.e., when their
slopes, *m1* and *m2*, are the same):

{% highlight scala %}
 case class Point(x: Double, y: Double)

 def inter(m1: Double, b1: Double, m2: Double, b2: Double): Point = {
   val x = (b2 - b1) / (m1 - m2)
   Point(x, m1 * x + b1)
 }
{% endhighlight %}

When the slopes are the same, the denominator, `m1 - m2` is `0`. So, you
might expect a divide-by-zero `ArithmeticException`. That's *not*
what happens, since we are calculating with `Double`s:

    scala> 1.0 / 0.0
    res0: Double = Infinity

    scala> 1 / 0

So you can't even catch this error with an exception handler, since no exception
is raised:

    scala> val r = inter(0, 0, 0, 0)
    r: Point = Point(NaN,NaN)

All these mechanisms for signalling errors share similar flaws:

1. **Exceptions:** you have to remember to catch them, or your program will
   crash. You can't tell if a function will throw an exception without carefully
   reading its code, which may not even be possible if it is in a library.

2. **Producing null:** even worse than exceptions, because your program will
   will *not* crash on an error. When it does crash, you'll spend a lot of
   time trying to figure out what produced the `null`.

3. **Producing other null-like values:** see above.

The real problem is that the types of all these functions are not useful:

- The type of `Integer.parseInt` is `String => Int`, but it may throw an exception
  instead of producing an `Int.

- The type of `ht.get` is `Any => String`, but it may produce a `null`.

- The type of `inter` is `(Double, Double, Double, Double) => Point`, but it
  can produce `Point(NaN, NaN)`, which is clearly not what we had in mind.

### A Solution

Let's use `inter` as an example and modify the function so that its type
makes it obvious that it may not always return a `Point`. We introduce
the following sealed trait:

{% highlight scala %}
sealed trait OptionalPoint
case class SomePoint(pt: Point) extends OptionalPoint
case class NoPoint() extends OptionalPoint
{% endhighlight %}

And we modify `inter` to produce `NoPoint` instead of a malformed-`Point`:

{% highlight scala %}
def inter(m1: Double, b1: Double, m2: Double, b2: Double): OptionalPoint = {
  if (m1 - m2 == 0) {
    NoPoint()
  }
  else {
    val x = (m1 - m2) / (b2 - b1)
    Point(x, m1 * x + b1)
  }
}
{% endhighlight %}

With this new type, any program that apply `inter` will be forced to check if
if a point was produced:

{% highlight scala %}
  inter(10, 3, 10, 3) match {
    case NoPoint => println("no intersection")
    case SomePoint(Point(x,y)) => println(s"intersection at ($x, $y)")
  }
{% endhighlight %}

Consider another example: a type that represents an *alarm clock*. An alarm
clock needs to track the current time and *the alarm time if the alarm is set*.
Here is a simple representation:

{% highlight scala %}
case class Time(h: Int, m: Int, s: Int)
case class AlarmClock(time: Time, alarm: Time, alarmOn: Boolean)
{% endhighlight %}

But, with this representation, it is easy to make simple errors. For example,
you may accidentally trigger the alarm when `time == alarm`, if you forget
to check `alarmOn` first. A cleaner represention is to use the same pattern we used
above:

{% highlight scala %}
 sealed trait OptionalAlarm
 case class NoAlarm() extends OPtionalAlarm
 case class AlarmSet(time: Time) extends OptionalAlarm

 case class AlarmClock(time: Time, alarm: OptionalAlarm)
{% endhighlight %}

In both `inter` and `AlarmClock`, it is completely obvious from the type
that a point is not always produced and that an alarm is not always set.
Partial functions are *pervasive* in computing and this pattern will make your
programs more robust.

But, it is annoying to define a new `OptionalPoint`, `OptionalAlarm`,
`OptionalFoo`, etc. for each type.

## The `Option[A]` Type

Instead, we can use generics to define the `Option` type:

{% highlight scala %}
sealed trait Option[A]
case class Some[A](x: A) extends Option[A]
case class None[A]() extends Option[A]
{% endhighlight %}

For example, here is `inter` rewritten to use `Option` instead of
`OptionalPoint`:

{% highlight scala %}
def inter(m1: Double, b1: Double, m2: Double, b2: Double): Option[Point] = {
  if (m1 - m2 == 0) {
    None[Point]()
  }
  else {
    val x = (m1 - m2) / (b2 - b1)
    Some[Point](Point(x, m1 * x + b1))
  }
}
{% endhighlight %}

We can make this program even more concise by relying on type inference. We
actually don't need to supply the `Point` type-argument to `None` and `Some`
constructors:

{% highlight scala %}
def inter(m1: Double, b1: Double, m2: Double, b2: Double): Option[Point] = {
  if (m1 - m2 == 0) {
    None()
  }
  else {
    val x = (m1 - m2) / (b2 - b1)
    Some(Point(x, m1 * x + b1))
  }
}
{% endhighlight %}

Since the result type of the function is `Option[Point]`, Scala can infer
that the type-argument to the constructors must be `Point` too.

*Note*: You *must* write the result type, `Option[Point]`. Scala cannot infer
the result type you expect in this case. But, once you write the type, it
will infer the type-arguments correctly in the body of `inter`.