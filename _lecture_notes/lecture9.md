---
layout: default
title: Lecture 9
---


Objectives
----------

1. Introduce classes and traits

1. Introduce bounded quantification (object with canonical compare operation)

1. How to change the comparator? (Wrapping vs. taking a comparator as an argument)

1. Show how to refactor old code


       Package Item pkg-item ::= ...
                               | trait TODO(arjun): traits as interfaces
                               | case class C[A...](x : T, ...) extends D { class-body* }

       Class Body class-body ::= def f(x : S, ...) : T = e            function definition

