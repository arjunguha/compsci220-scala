---
layout: lecture
title: Introduction to Scala
---

## Why Scala?

- You only know Java and that alone is not enough

- Lots of interesting systems written in other languages

- Lots of important people think you should learn other languages

- Scala borrows ideas from many other languages, so the concepts you learn
  will be broadly applicable, even to Java

- Smooth path from Java (haha, what crap)

## Why the Console?

- You've only used Eclipse so far

- Eclipse is very powerful, but abstracts away lots of details of programming
  languages

- Using the console, you'll learn what lies "under the hood" of Eclipse.
  Eclipse is not a perfect abstraction. It can break down, and when it does,
  you'll need to understand the details we'll learn in this class

- Lots of very powerful tools command line tools, you'll see only a few of them

- Many advanced systems (e.g., the operating systems, the C compiler, etc.) can
  only be built using command-line tools.

# The Scala REPL

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
you can define them very tersely as shown above. But, most interesting
functions are longer and need local variables.

## Local Variables

[FILL]


### Java Comparison

[FILL] show methods

In Java, all code must be organized into classes, Scala imposes no such
restriction (Scala does have classes and objects and we will introduce them
shortly). For example, you've already seen how to make variables, without
any need for a class:

~~~
val mersenne = 524287
val courseName = "Programming Methodology"
~~~

The equivalent code in Java would be:

~~~
public class Main {
  int mersenne = 524287
  String courseName = "Programming Methodology"
}
~~~

## A Preview of Classes

## Case Classes








