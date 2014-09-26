---
layout: lecture
title: Lecture 8 (Balancing
---


Objectives
----------

1. SBT

1. ScalaTest

1. Introduce this fragment of Scala:

       Package      pkg      ::= package dotted.package.name; pkg-item*

       Package Item pkg-item ::= private[id] sealed trait C\[A ...]
                               | private[id] case class C\[A](x : T, ...) extends C'\[A]
                               | class TestSuite extends org.scalatest.FunSuite { test* }

       Test         test     ::= test(description) { e* }




Objectives
----------

1. AVL trees, without data abstractions

1. Show that without types, invariants can be broken

1. Big-O
