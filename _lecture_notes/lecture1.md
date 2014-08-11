---
layout: default
title: Lecture 1
---

Objectives
----------

1. Introduce the course VM.

1. Introduce the command line (`mkdir`, `cd`, and, `ls`).

1. Introduce the `scala220` REPL.

1. Introduce this fragment of Scala:

        Types T ::= Int | Boolean | String | Double | Classname

        Identifiers f, x, C ::=

        Constants c ::= true| false
                      | ... | -1 | 0 | 1 | ...
                      | ... | -1.0 | 0.0 | 1.0 | ...
                      | "string" | ...

        Patterns pat ::= c
                      | x
                      | C(pat1, ..., patn)

        Expressions e ::= c                                    constants
                        | e1 op e2                             infix operators
                        | f(e1, ..., en)                       function application
                        | e.x                                  field lookup
                        | C(e1, ..., en)                       create case class
                        | { val x1 = e1; ...; val xn = en; e } variable binding
                        | e match { case pat => e; ... }       pattern matching
                        | def f(x : S, ...) : T = e            function definition

        Imports import ::= import pkg.x
                         | import pkg._

        Top Level top ::= case class C(x1 : T1, ..., xn : Tn)
                        | val x = e
                        | e

        Programs  program ::= import1; ...; importn; top1; ...; topn

1. Introduce some simple graphics expressions.

Lecture Outline
---------------

- Course overview

- Why Scala

- Simple arithmetic expressions in the REPL

  - Simple arithmetic examples

  - Defining over numbers in the REPL

  - Importing the `math._` library for more functions

- Loading files into the REPL and the text editor (Sublime Text)

- Defining case classes (e.g., ball and point)

- Some drawing primitives (e.g., write functions to draw ball and point)

- Pattern matching

  - Avoid writing ball.x, ball.y, etc.

  - Can pattern match to branch on boolean values
