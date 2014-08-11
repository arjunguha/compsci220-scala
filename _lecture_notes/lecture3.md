---
layout: default
title: Lecture 3
---

1. More list processing functions

1. Generic data structures and generic functions

1. Functions as values / strategy pattern

1. Introduce this fragment of Scala:

       Types       T ::= ...
                      | A
                      | (S, ...) => T


       Expressions e ::= ...
                        | def f[A, ...](x : S, ...) : T = ... generic function

      Top Level top ::= ...
                      | case class C\[A](x : T, ...)
                      | sealed trait C\[A]
                      | case class C\[A](x : T, ...) extends C'\[A]


Lecture Outline
---------------

- More list processing functions (e.g., sorting, searching sorted lists, etc.)

- Recognizing when code can be abstracted

  - E.g., Comparator in sort

  - E.g., filtering predicate in filter

  - E.g., Mapped function in map

  - E.g., Combining function in sum

  - E.g., Combining function in ifAll and ifAny

- Recognizing when a type can be abstracted

  - E.g., type of element in a list

  - E.g., type of result from combining function

