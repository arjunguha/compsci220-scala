---
layout: lecture
title: Lecture 3
---

## The `Option[A]` type

1. *How do we calculate the poInt where two lines Intersect?*

       y = m1.x + b1
       y = m2.x + b2

       m1.x + b1 = m2.x + b2

       (m1 - m2).x = b2 - b1

       x = (b2 - b1) / (m1 - m2)

       y = m1.((b2 - b1) / (m1 - m2)) + b2

    In code:

       case class Point(x: Double, y: Double)

       def Inter(m1: Double, b1: Double, m2: Double, b2: Double): Point = {
         val x = (b2 - b1) / (m1 - m2)
         PoInt(x, m1 * x + b1)
       }

1. *When can go wrong?*

1. Throw an exception is okay, but you have to remember to handle it. Even in
   Java, this is an `ArithmeticException`, which is a `RuntimeException`.

1. Instead, can we change the type of `Inter`, so that it is obvious when
   the lines do not Intersect?

   - Recap `sealed trait` and pattern matching

   - Use the following type:

         sealed trait OptionalPoInt
         case class SomePoInt(pt: PoInt) extends OptionalPoInt
         case class NoPoInt() extends OptionalPoInt

   - Refactor as follows:

         def Inter(m1: Double, b1: Double, m2: Double, b2: Double): PoInt = {
           if (m1 - m2 == 0)
             NoPoInt()
           else {
             val x = (m1 - m2) / (b2 - b1)
             PoInt(x, m1 * x + b1)
           }
         }

1. A program that calls `Inter`, will be forced to check:

       import cmpsci220.graphics._

       def triangle(m1: Double, b1: Double,
                    m2: Double, b2: Double,
                    m3: Double, b4: Double): Image =
         (Inter(m1, b1, m2, b2), Inter(m2, b2, m3, b3), Inter(m3, b3, m1, b1)) match {
           case (SomePoInt(pt1), SomePoInt(pt2), SomePoInt(p3)) =>
             overlay(move(line(pt2.x, pt2.y), pt1.x, pt1.y),
                     move(line(pt3.x, pt3.y), pt2.x, pt2.y),
                     move(line(pt1.x, pt1.y), pt3.x, pt3.y))
           case _ => blank
         }

1. How many `SomePoInt` values exist? How many `NoPoint` values are there?

1. Another example: Recap unary numbers and the function `def IntToI(n: Int): I`
   that can throw an exception.

1. Refactor to use a monomorphic option:

       sealed trait OptionalI
       case class SomeI(i: I) extends OptionalI
       case class CannotBuildI() extends OptionalI

       def IntToOptionalI(n: Int): I =
         if (n < 0) CannotBuildI else OptionalI(IntToI(n))

1. In the *clocks* homework assignment, the `AlarmClock` type could be written
   using an `OptionalTime`:

       sealed trait OptionalAlarm
       case class NoAlarm() extends OPtionalAlarm
       case class AlarmSet(time: Time) extends OptionalAlarm

       case class AlarmClock(time: Time, alarm: OptionalAlarm)

1. Introduce our Option[A]. *Actual Scala `Option` relies on variance, so we use
   our own definition*.

       sealed trait Option[A]
       case class Some[A](x: A) extends[A]
       case class None[A] extends[A]

1. You will see this type *everywhere*. Partial functions are very common and
   `Option[A]` makes partialness explicit, so you don't make stupid mistakes.

1. Anti-pattern, having a `None`-like value in the type:

       def indexOfHelper(lst: List[A], elt: A, index: Int): Int = lst match {
         case Empty() => -1
         case Cons(head, tail) =>
           if (head == elt) {
             index
           }
           else {
             loop(tail, elt, index + 1)
           }
       }

       def indexOf[A](lst: List[A], elt: A): Int = indexOfHelper(lst, elt, 0)


1. Obvious refactoring:

       def indexOfHelper(lst: List[A], elt: A, index: Int): Option[Int] = lst match {
         case Empty() => None()
         case Cons(head, tail) =>
           if (head == elt) {
             Some(index)
           }
           else {
             loop(tail, elt, index + 1)
           }
       }

       def indexOf[A](lst: List[A], elt: A): Option[Int] = indexOfHelper(lst, elt, 0)

1. Simpler version, if you start with `Option[Int]` in mind:

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

1. Quickly, what's wrong with this simpler code? Blows the stack. We'll
   understand this in detail later. Don't worry about it for now.

## List Processing (map function)

1. Let's write some simple list-processing functions:

       def add1ToAll(lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(head + 1, add1ToAll(tail))
       }

       def squareAll(lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(head * head, add1ToAll(tail))
       }

       def doubleAll(lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(head * 2, add1ToAll(tail))
       }


1. What do these functions have in common? They have exactly the same structure,
   but they only vary the operation applied to the head:

       // Monomorphic lst
       def map(lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(f(head), map(tail))
       }

       def f1(n: Int): Int = n + 1

       def f2(n: Int): Int = n * n

       def f3(n: Int): Int = n * 2

1. We need to send in `f` as an argument  and change the recursive call:

       def map(f: Int => Int, lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(f(head), map(f, tail))
       }

       map(f1, lst)
       map(f2, lst)
       map(f3, lst)

1. Another list processing function:

       def lengthAll(lst: List[String]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(head.length, add1ToAll(tail))
       }

       def f4(str: String): Int = str.length

   Why can't we write the following?

       map(f4, lst)

1. The type of `map` requires a `List[Int]`, but does the body ever
   rely on the elements being `Int`s? All the `Int`-requiring code has been
   factored out into `f1`, `f2`, and `f3`. Similarly for the return type.

   So, we can introduce type-parameters:

       def map[A,B](f: A => B, lst: List[A]): List[B] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) => Cons(f(head), map[A,B](f, tail))
       }

       map[Int, Int](f1, lst)
       map[Int, Int](f2, lst)
       map[Int, Int](f3, lst)
       map[String, Int](f4, lst)

1. The type arguments don't have to be specified. Scala can *usually* infer
   them. We've been working with inference already: constructor type parameters
   have been inferred.

       def map[A,B](f: A => B, lst: List[A]): List[B] = lst match {
          case Empty() => Empty()
          case Cons(head, tail) => Cons(f(head), map(f, tail))
        }

        map(f1, lst)
        map(f2, lst)
        map(f3, lst)
        map(f4, lst)

## Definitions

**Higher-order function**: a function that consumes another function as an
argument.

**First-class functions**: a property of a programming language. A programming
language has first-class functions, if functions are values. i.e., they
have the same status as primitive types, such as numbers and strings.

E.g., you can write a higher-order function in C, but C does not have first-
class functions.

## More list processing: filter

1. Another pattern:

       def filterOdd(lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) =>
           if (head % 2 == 0) {
             Cons(head, filterOdd(tail))
           }
           filterOdd(tail)
       }

     Similarly, define `filterEven`

1. What's the operation on `head`?

       def f1(n: Int): Boolean = n % 2 == 0
       def f2(n: Int): Boolean = n % 2 == 1

1. The `filter` function:

       def filter(pred: Int => Boolean, lst: List[Int]): List[Int] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) =>
           if (pred(head)) {
             Cons(head, filter(tail))
           }
           filter(tail)
       }

1. Again, the type can be generalized, since everything that is `Int`-specific
   has been factored out:

       def filter[A](pred: A => Boolean, lst: List[A]): List[A] = lst match {
         case Empty() => Empty()
         case Cons(head, tail) =>
           if (pred(head)) {
             Cons(head, filter(tail)) // type arguments inferred
           }
           filter(tail)
       }

1. How would we test this function? What properties matter?

       def alwaysTrue[A](x: A) = true

       def alwaysFalse[A](x: A) = false

       forall A lst . filter[A](alwaysTrue, lst) == lst

       forall A lst . filter[A](alwaysFalse, lst) == Empty()

       forall A lst . map(pred, filter[A](pred, lst)) /* produces a list of trues */

## List Processing: find

1. Another function:

       // Returns the first even number in the list
       def findEven(lst: List[Int]): Int = lst match {
         case Cons(head, tail) => if (head % 2 == 0) head else findEven(tail)
         case Empty() => -1
       }

   *We are going to make this generic, but what else is wrong with this
   function?*

1. Refactored:

       def find[A](pred: A => Boolean, lst: List[A]): Option[A] = lst match {
         case Cons(head, tail) => if (pred(head)) Some(head) else findEven(tail)
         case Empty() => None()
       }

## Older notes below (ignore for now)

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


## Lecture Outline


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

