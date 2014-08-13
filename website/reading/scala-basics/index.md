---
layout: lecture
title: Scala Basics
---

## Background Reading

For the first few weeks of the class, we will use the Linux command-line before
graduating to more sophisticated tools. The course virtual machine has a program
called "LXTerminal", which you should use.

Unless you're familar with the Linux command-line, you must read  Zed Shaw's
[Command Line Crash Course](http://learncodethehardway.org/cli/book/cli-crash-
course.html) upto and including the chapter on *Removing a File (rm)*. Zed likes
to swear at his own readers, so we'd like to apologize in advance on his behalf.

## The Scala REPL

Like many modern languages, Scala has a *REPL*, which you can use to evaluate
expressions immediately. To launch the Scala REPL, start a terminal,
type in `scala220` and press enter. Your screen should look like this:

~~~
student@vm:~$ scala220
Welcome to Scala version 2.11.2 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_05).
Type in expressions to have them evaluated.
Type :help for more information.

scala>
~~~

The `scala>` prompt indicates that you can type in Scala expressions to
evaluate.

## Simple Expressions and Names

Arithmetic in Scala is very similar to arithmetic in Java:

~~~
scala> 19 * 17
res0: Int = 323
~~~

Strings in Scala will also look familiar:

~~~
scala> "Hello, " + "world"
res1: String = Hello, world
~~~

Boolean expressions will be familar too:

~~~
scala> true && false
res2: Boolean = false
~~~

Let's examine the last interaction more closely. When you type `true && false`,
Scala prints three things:

1. An automatically-generated *name* (`res2`),
2. The *type* of the expression (`Boolean`), and
3. The *value* of the expression (`false`).

On the Scala REPL, you can use the generated name as variable. But, you're
better off picking meaningful names yourself using `val`:

~~~
scala> val mersenne = 524287
mersenne : Int = 524287
scala> val courseName = "Programming Methodology"
courseName : String = Programming Methodology
~~~

### Type Inference

In this class, you'll find that Scala programs are significantly shorter than
their Java counterparts. For exmaple, in the variable definitions above, you did
not have to write any types. Instead, Scala *inferred* them for you. This
feature is very helpful in larger programs, where types can become complex.

Type inference is not magic; later in the course, you'll learn more about how it
works and when it doesn't. For now, here's a rule of thumb: Scala can infer the
type of variable named with `val`. But, Scala *cannot* infer the type of
function parameters.

## Functions

Here is a very simple Scala function:

~~~
scala> def double(n: Int): Int = n + n
double: (n: Int)Int
~~~

This code defines a function called `double`, which takes an argument called
`n` of type `Int` and returns a value of type `Int`. We can apply the
function as follows:

~~~
scala> double(10)
res3: Int = 20
~~~

The following function takes two arguments, `x` and `y`, and calculates the
distance from the point *(x,y)* to the origin:

~~~
scala> def dist(x: Double, y: Double): Double = math.sqrt(x * x + y * y)
dist: (x: Double, y: Double)Double

scala> dist(3.0, 4.0)
res4: Double = 5.0
~~~

Notice that unlike variable definitions, we need *type annotations*
on function parameters and result types.

If your function actually fits on a line (without scrolling off your window),
you can define them very tersely as shown above. But, many interesting
functions span several lines and need local variables.

## Blocks and Local Variables

You can define local variables with a *block*. A block is code delimted by
curly-braces. For example:

~~~
def dist2(x: Double, y: Double): Double = {
  val xSq = x * x
  val ySq = y * y
  math.sqrt(xSq + ySq)
}

dist2(3.0, 4.0)

~~~

### Saving Code in Files

You can type this code into the Scala REPL, but it cumbersome. Moreover, if you
make a mistake on one line, you have to abort and type in the entire function
again. You'll find it easier to write this code using a text editor and saving
it as a file. Try to save the code above in a file called `intro.scala`. You can
load the code as follows:

~~~
scala> :load intro.scala
Loading intro.scala...
dist2: (x: Double, y: Double)Double
res5: Double = 22.360679774997898
~~~

### Java Comparison

In Java, all code must be organized into classes, Scala imposes no such
restriction (Scala does have classes and objects and we will introduce them
shortly). For example, the Java analogue of `intro.scala` is the following
class:

~~~
public class Intro {

  static double dist2(double x, double y) {
    int xSq = x * x;
    int ySq = y * y;
    return Math.sqrt(xSq + ySq);
  }

  static double res0 = dist2(3.0, 4.0);
}
~~~

## Classes (Preview)

Scala has classes, just like Java, and it supports some advanced features
that are beyond the scope of Java classes. Like what we've seen so far,
Scala classes tend to be shorter than their Java counterparts. For example,
here is a simple Java class that represents a point:

~~~
public class Point {

  double x;
  double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

}
~~~

Here is an equivalent class in Scala:

~~~
scala> class Point(x : Double, y : Double)
defined class Point
~~~

We can create an instance of a class using `new`, just as in Java:

~~~
scala> new Point(1, 2)
res1: Point = Point@6d8a00e3
~~~

Instead of using classes, we are going to use *case classes*, which
are a unique feature of Scala that do not exist in Java.

## Case Classes

Instead of writing `class`, we are going to write `case class`:

~~~
scala> case class Point(x: Double, y: Double)
defined class Point
~~~

Although the "case" makes the definition a word longer, it makes Points slightly
easier to use: (1) we can now omit `new` when we define new values and (2)
these values display on screen in a natural way:

~~~
> Point(1, 2)
res2: Point = Point(1.0,2.0)
~~~

Compare this output to the output `new Point(1, 2)` above.

Under the hood, Scala is automatically overriding the `toString` method for
`Point` with a convenient definition.

In fact, if a case-class field refers to another case-class, the generated
`toString` function still behaves naturally:

~~~
scala> case class TwoPoints(p1 : Point, p2: Point)
defined class TwoPoints

scala> TwoPoints(Point(1, 2), Point(3, 4))
res4: TwoPoints = TwoPoints(Point(1.0,2.0),Point(3.0,4.0))
~~~

Of course, you can override `toString` yourself if you desire. But, for the
first few weeks of the class, we will use case classes as-is. Not only
are case-classes convenient for the reasons discussed above, but they support
another powerful feature called *pattern matching*, which has no analog in
Java.
