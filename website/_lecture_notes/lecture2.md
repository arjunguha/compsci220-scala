---
layout: lecture
title: Lecture 2
---

Objectives
----------

1. Introduce this fragment of Scala:

       Top Level top ::= ...
                      | sealed trait C
                      | case class C(x1 : T1, ... xn : Tn) extends C'
                      | test(str) e
                      | fail(str) e

1. Introduction to unit testing

1. Introduction to defining auxiliary functions and constants

1. Introduction to defining data by cases and structural recursion

Lecture Outline
---------------

1. Pattern-matching recap (since everything else is like Java)

   - Can pattern-match on any value, including boolean values

1. Unit testing functions and examples

1. Writing auxiliary functions (HtDP pg. 25) and using constants

1. Defining data by cases

   - E.g., different kinds of shapes

   - E.g., lists and some basic list processing functions

   - E.g., arithmetic expressions and evaluation
