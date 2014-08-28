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

Like many modern languages, Scala has a *REPL* (read-eval-print loop), which you
can use to evaluate expressions immediately. To launch the Scala REPL, start a
terminal, type in `scala220` and press enter. Your screen should look like this:

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

On the Scala REPL, you can use the generated name as a variable. But, you're
better off picking meaningful names yourself using `val`:

~~~
scala> val mersenne = 524287
mersenne : Int = 524287
scala> val courseName = "Programming Methodology"
courseName : String = Programming Methodology
~~~

### Type Inference

You'll find that Scala programs are significantly shorter than their Java
counterparts. A key feature of Scala that lets you write less code is *type
inference*. Notice in the variable definitions above, you did not have to write
any types. Instead, Scala *inferred* them for you. This feature is very helpful
in larger programs, where types can become complex.

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

You can define local variables within a *block*. A block is code delimited by
curly-braces. For example:

~~~ scala
def dist2(x: Double, y: Double): Double = {
  val xSq = x * x
  val ySq = y * y
  math.sqrt(xSq + ySq)
}

dist2(3.0, 4.0)
~~~

## Saving Code in Files

You can type this code into the Scala REPL, but it is cumbersome. Moreover, if you
make a mistake on one line, you have to abort and type in the entire function
again. You'll find it easier to write this code using a text editor and save
it as a file. Try to save the code above in a file called `intro.scala`. You can
load the code as follows:

~~~
scala> :load intro.scala
Loading intro.scala...
dist2: (x: Double, y: Double)Double
res5: Double = 22.360679774997898
~~~

## Additional Reading

Read Chapter 2, "First Steps in Scala" from *Programming in Scala*.
Some of this material is a preview of things to come (Steps 5 and 6).

{% include links.md %}