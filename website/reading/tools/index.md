---
layout: lecture
title: Professional Scala Tools
---

So far, you've been programming using the `cmpsci220` library and the `scala220`
program. These were designed to make learning Scala easier for this course.
But, it is now time to start using professional tools.

Instead of reading a dumbed-down course notes, you're going to read
read the guides that come with the tools themselves.

## Programming in Scala

Read Chapters 4--6 and Chapter 13 of *Programming in Scala*. These chapters
will introduce you to programming with objects and using Scala's built-in
libraries.

## sbt (Scala Build Tool)

Instead of using the `scala220` console, you're going to use [sbt]. SBT is
already installed on the course VM.

Required Reading:

-  Chapters 1--4 of the [sbt tutorial]. The tutorial assumes you're very
familiar with the Linux command line. If you're still shaky, refer to
the [command-line crash course] that you read in the first week.

## ScalaTest

Instead of using the testing functions in the `cmpsci220` package, you're
going to use [ScalaTest].

Required Reading:

- The [ScalaTest Quick Start] guide. In particular, pay attention to how to
  use ScalaTest with sbt.
- The [FunSuite] guide. ScalaTest supports several testing styles. FunSuite
  is extremely lightweight and the style that I will use in class. However,
  you are free to use other styles if you like.


[sbt]: http://www.scala-sbt.org/index.html
[ScalaTest]: http://www.scalatest.org
[sbt tutorial]: http://www.scala-sbt.org/0.13/tutorial/index.html
[FunSuite]: http://doc.scalatest.org/2.2.1/index.html#org.scalatest.FunSuite
[command-line crash course]: http://learncodethehardway.org/cli/book/cli-crash-course.html
[ScalaTest Quick Start]: http://www.scalatest.org/quick_start
[FunSuite]: http://doc.scalatest.org/2.2.1/index.html#org.scalatest.FunSuite