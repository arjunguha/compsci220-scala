---
layout: lecture
title: Lecture 8
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
