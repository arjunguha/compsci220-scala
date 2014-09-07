---
layout: lecture
title: Lecture 4
---

## Binary Trees

1. The following type:

       sealed trait BinTree[A]
       case class Leaf() extends BinTree[A]
       case class Node(lhs: BinTree[A], x: A, rhs: BinTree[A]) extends BinTree[A]

1. Last lecture, we defined `map` for lists. We can define `mapBinTree` by
   following the same pattern:

       def mapBinTree[A, B](f: A => B, tree: BinTree[A]): BinTree[B] = tree match {
         case Leaf() => Leaf()
         case Node(lhs, x, rhs) => Node(mapBinTree(f, lhs), f(x), mapBinTree(f, rhs))
       }

1. It makes sense to define `map` for any container. For example, `Option[A]`
   is a container that holds either zero elements or one element.

       def mapOption[A, B](f: A => B, opt: Option[A]): Option[B] = tree match {
         case None() => None()
         case Some(x) => Some(f(x))
       }

1. This can make certain functions easier to write. For example, we wrote this
   function last lecture:

       def indexOf[A](lst: List[A], elt: A): Option[Int] = lst match {
         case Empty() => None()
         case Cons(head, tail) =>
           if (head == elt) {
             Some(index)
           }
           else {
             indexOf(tail, elt) match {
               case Some(n) => Some(n + 1)
               case None() => None()
             }
           }
       }

    We can refactor using `mapOption`:

        def add1(n: Int): Int = n + 1

        def indexOf[A](lst: List[A], elt: A): Option[Int] = lst match {
          case Empty() => None()
          case Cons(head, tail) =>
            if (head == elt) Some(index)
            else indexOf(tail, elt).map(add1)
        }

1. How do we write `find` for `BinTree`? *No ordering assumption*.

       def findBinTree[A](pred: A => Boolean, tree: BinTree[A]): Option[A] = tree match {
         case Leaf() => None()
         case Node(lhs, x, rhs) =>
           if (pred(x)) {
             Some(x)
           }
           else {
             findBinTree(pred, lhs) match {
               case Some(v) => Some(v)
               case None() => binBinTree(pred, rhs)
             }
           }
       }

1. How do we write `filter` for `BinTree`? *We have to filter out subtrees
   whose roots do not match.*

       def filterBinTree[A](pred: A => Boolean, tree: BinTree[A]): BinTree[A] = tree match {
         case Leaf() => Leaf()
         case Node(lhs, x, rhs) =>
           if (pred(x)) {
             Node(filterBinTree(pred, lhs), x, filterBinTree(pred, rhs))
           }
           else {
             Leaf()
           }
       }

1. If we want the property that all elements in the tree that `filterBinTree`
   produces satisfy `pred`, then we are *forced* to produce `Leaf()` above.
   (*Seriously, don't return null.*)

## Binary Search Trees

1. Binary search trees with integer keys:

       sealed trait BST[A]
       case class Leaf() extends BST[A]
       case class Node(lhs: BST[A], k: Int, v: A, rhs: BST[A]) extends BST[A]

1. What should be the type of `findBST`? What if the element does not exist?

       def lookup[A](bst: BST[A}, key: Int): Option[A] = bst match {
         case Leaf() => None()
         case Node(lhs, k, v, rhs) =>
           if (key == k) Some(v)
           else if (key < k) lookup(lhs, key)
           else lookup(rhs, key)
       }

1. Define `mapBST`. Leaves the keys intact.