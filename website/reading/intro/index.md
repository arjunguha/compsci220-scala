---
layout: lecture
title: Introduction
---

*Course Description*: Development of individual skills necessary for designing,
implementing, testing and modifying larger programs, including: use of
integrated design environments, design strategies and patterns, testing, working
with large code bases and libraries, code refactoring, and use of debuggers and
tools for version control.

We have revamped CS220 *Programming Methodology* this year. We are going to
study all the concepts above, but in the context of a modern programming
language: [Scala]. Scala lets you write exactly the same kind of object-oriented
code that you've seen in Java. In fact, Scala code and Java code can seamlessly
co-exist and interoperate in the same program; we'll leverage this feature later
in the course. So, design strategies and patterns that you will learn in this
course will be applicable to Java and Scala. However, you will find that Scala
programs in Scala are typically much shorter than their Java counterparts.
With a little experience, we think you'll find Scala easier to read and write.

The main reason we're using Scala, is to expose you to programming techniques
and language features that are beyond the scope of Java. The truth is that
modern software systems are written in a plethora of languages. Moreover, large
systems tend to use several programming languages. Therefore, to succeed in your
computing career, you have to be familiar with several languages and be able to
learn new languages on your own. [New programming languages] are constantly
invented and it is impossible to predict the next big language that everyone
will use or the language you'll need to learn for an exciting internship or job.

Scala is a big language with many unique features and we are not going to learn
to use them all. Instead, we are going to focus on ideas that Scala shares with
other modern programming languages. Here are some of the key ideas that we will
cover in this course that go beyond Java:

- **[First-class functions]** are a feature of almost every modern language and
  the cornerstone of *functional programming.* They are pervasive in JavaScript,
  Ruby, Python, and other popular scripting languages.

  In fact, even [Java 8](http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
  and [C++11](http://msdn.microsoft.com/en-us/library/dd293608.aspx) have
  limited support for first-class functions.

- **[Algebraic data types]** are available in Apple's [Swift], Mozilla's [Rust],
  Microsoft's [F#], and several other programming languages. Programming with
  algebraic data types is very different from programming in an object-oriented
  style. You'll cover both styles of programming in this course and develop
  a deep understanding of the tradeoffs.

- **[Type inference]** is available in many modern typed programming languages
  (and in a limited form in [Java 7](http://docs.oracle.com/javase/tutorial/java/generics/genTypeInference.html)).
  As the name suggests, in a language with type inference, the compiler can
  often "infer" elided types. So, your programs become shorter, but retain all
  the advantages of type checking.

The main themes of the course are not language-specific. We will emphasize
the following broad ideas that are applicable in all software development:

- **Testing** is critical for building reliable software. You will learn how
  to think like a tester and make effective use of testing tools and frameworks.
  Every programming problem you solve in this course will have to be tested.
  A significant part of your grades will be determined by the quality of
  your tests.

- **Design patterns** are recipes for solving typical programming problems.
  You'll learn several object-oriented design patterns that are applicable to
  Java. In addition, you'll also learn several functional design patterns
  (exploiting the ideas listed above) that are applicable to a variety of
  modern languages.

- **Refactoring** is a key concept that we emphasize throughout the course.
  As we introduce new ideas, we will systematically refactor our old code to
  exploit them.

- **Debugging** is a necessary skill because even small programs often have
  bugs. You will learn to effectively use the debugger that is built-in to the
  IDE.

- **Command-line tools** such as compilers and build tools lie under-the-hood of
  sophisticated IDEs such as Eclipse. Learning to use the command-line will
  make you a better IDE user. Moreover, many advanced systems (e.g, operating
  systems and Web browsers) are typically built using the command-line directly.

- **Version control** software is critical for collaborative software development
  and used by all professional programmers. Although you will be programming
  alone in the course, version control will still help you organize your
  programming and save you a lot of time if you accidentally delete or break
  your code.

- **Using libraries** is critical for writing software that gets real work done.
  You will start by using a simple graphics library that we've developed for
  the course. By the end of the course, you'll know how to discover and use
  software libraries on the Web.

## Recommended Reading

There are many classic articles on programming that you should read.
Many of thesea I encourage you to read
at your leisure.

- [Paul Graham's essays](http://paulgraham.com/articles.html). The earlier
  essays are particularly pertinent, E.g.,
  [Beating the Averages](http://paulgraham.com/avg.html) and
  [Being Popular](http://paulgraham.com/popular.html).

- [Joel Spolsky's blog](http://www.joelonsoftware.com). E.g.,
  [Advice for Computer Science College Students](http://www.joelonsoftware.com/articles/CollegeAdvice.html)
  and [Getting Your Resume Read](http://www.joelonsoftware.com/articles/ResumeRead.html).

- [XKCD](http://xkcd.com) comics will make more sense after you take this course.

[Scala]: http://www.scala-lang.org/what-is-scala.html
[First-class functions]: http://en.wikipedia.org/wiki/First-class_function
[Algebraic data types]: http://en.wikipedia.org/wiki/Algebraic_data_type
[Swift]: https://developer.apple.com/swift/
[Rust]: http://www.rust-lang.org
[F#]: http://msdn.microsoft.com/en-us/library/dd233154.aspx
[Type inference]: http://en.wikipedia.org/wiki/Type_inference
[New programming languages]: http://www.oreillynet.com/pub/a/oreilly/news/languageposter_0504.html
[Design patterns]: http://en.wikipedia.org/wiki/Software_design_pattern
[Refactoring]: http://en.wikipedia.org/wiki/Code_refactoring
