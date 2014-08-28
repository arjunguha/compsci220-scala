package cmpsci220.hw.measurement

import cmpsci220._

object OrdList {

  def insert[A](compare : (A, A) => Order, n : A, lst : List[A]) : List[A] = lst match {
    case Empty() => Cons(n, Empty())
    case Cons(m, rest) => compare(n, m) match {
      case LT() => Cons(n, Cons(m, rest))
      case GT() => Cons(m, insert(compare, n, rest))
      case EQ() => Cons(m, rest)
    }
  }

  def isMember[A](compare : (A, A) => Order, n : A, lst : List[A]) : Boolean = lst match {
    case Empty() => false
    case Cons(m, rest) => compare(n, m) match {
      case LT() => false
      case GT() => isMember(compare, n, rest)
      case EQ() => true
    }
  }

}
