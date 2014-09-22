---
layout: lecture
title: Lecture 7 (Real Tools)
---

## What are class files?

1. Write the singleton pattern in Java using Eclipse. Here are some epic
   slides to go with it:

   http://www.slideshare.net/jonsimon2/introduction-to-design-patterns-and-singleton

   E.g., an in-memory log.

1. Show the .class files that Eclipse generates.

1. Run `javap -c` on the class files.

1. Show how to use `javac` and run a class file using `java`

## Where are the .class files in Scala?

1. The Scala REPL deoes not create class files.

1. Show how to use `scalac`.

1. Show that `java` cannot run the class file, since it relies on the Scala
   library.

## Introduction to SBT and ScalaTest

1. Show the build.sbt file with ScalaTest support.

1. Show the singleton pattern in Scala for an object that just has the log.

1. Show a test for the log using ScalaTest

1. Show the sbt console commands

1. Show the `target/` directory

1. Show nested packages and that the filenames not relevant

## Explain the REPL and Scala220

1. Functions in `scala220` are implicitly in an object

1. Show the contents of the `scala220` script: the classpath flag

1. Unzip the `.jar` file

## Introduce the HW assignment

Wing it.