//
// Preliminaries
//

import cmpsci220._
import cmpsci220.hw.recursion._
//
// Part 1
//

def map2[A,B,C](f: (A, B) => C, lst1: List[A], lst2: List[B]): List[C] =
  (lst1, lst2) match {
    case (Empty(), _) => Empty()
    case (_, Empty()) => Empty()
    case (Cons(head1, tail1), Cons(head2, tail2)) =>
      Cons(f(head1, head2), map2(f, tail1, tail2))
  }

test("map2 with add") {
  def add(x: Int, y: Int): Int = x + y
  assert(map2(add, List(1, 2, 3), List(4, 5, 6)) == List(5, 7, 9))
}

def zip[A,B](lst1: List[A], lst2: List[B]): List[(A, B)] =
  map2((x:A, y:B) => (x, y), lst1, lst2)

test("zip test") {
  assert(zip(List(1, 2, 3), List(4, 5, 6)) == List((1,4), (2, 5), (3, 6)))
}

def append[A](lst1: List[A], lst2: List[A]): List[A] = lst1 match {
  case Empty() => lst2
  case Cons(head, tail) => Cons(head, append(tail, lst2))
}

def flatten[A](lst: List[List[A]]): List[A] = lst match {
  case Empty() => Empty()
  case Cons(head, tail) => append(head, flatten(tail))
}

test("flatten test") {
  assert(flatten(List(List(1, 2), List(3, 4))) == List(1, 2, 3, 4))
}

def flatten3[A](lst: List[List[List[A]]]): List[A] = flatten(flatten(lst))

def buildHelper[A](ix: Int, length: Int, f: Int => A): List[A] =
  if (ix < length) {
    Cons(f(ix), buildHelper(ix + 1, length, f))
  }
  else {
    Empty()
  }

def build[A](length: Int, f: Int => A): List[A] = buildHelper(0, length, f)

test("build test") {
  def f(x: Int) = x
  assert(build(10, f) == List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
}
def mapList[A, B](lst: List[A], f: A => List[B]): List[B] = flatten(map(f, lst))

test("mapList test") {
  def id(x: Int): Int = x
  def f(n: Int): List[Int] = buildList(n, id)
  assert(mapList(List(1, 2, 3), f) == List(1, 2, 2, 3, 3, 3))
}

//
// Don't have to write this part, but it is defined in the assignment
//
type SlowQueue[A] = List[A]

def emptySlow[A](): SlowQueue[A] = Empty()

def enqueueSlow[A](elt: A, q: SlowQueue[A]): SlowQueue[A] = q match {
  case Empty() => List(elt)
  case Cons(head, tail) => Cons(head, enqueueSlow(elt, tail))
}

def dequeueSlow[A](q: SlowQueue[A]): Option[(A, SlowQueue[A])] = q match {
  case Empty() => None()
  case Cons(head, _) => Some((head, tail))
}

//
// Part 2
//

def enqueue[A](elt: A, q: Queue[A]): Queue[A] = Queue(q.front, Cons(elt, q.back))

def dequeue[A](q: Queue[A]): Option[(A, Queue[A])] = q match {
  case Queue(Cons(head, tail), back) => Some((head, Queue(tail, back)))
  case Queue(Empty(), back) => reverse(back) match {
    case Empty() => None()
    case Cons(head, tail) => Some((head, Queue(tail, Empty())))
  }
}
