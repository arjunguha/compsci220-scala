package hw.measurement

sealed trait List[A]
case class Nil[A]() extends List[A]
case class Cons[A](head : A, tail : List[A]) extends List[A]

object List {

  def compareInt(x : Int, y : Int) : Order = {
    if (x < y) LT()
    else if (x > y) GT()
    else EQ()
  }

  def empty[A] : List[A] = Nil()

  def insert[A](compare : (A, A) => Order, n : A, lst : List[A]) : List[A] = lst match {
    case Nil() => Cons(n, Nil())
    case Cons(m, rest) => compare(n, m) match {
      case LT() => Cons(n, Cons(m, rest))
      case GT() => Cons(m, insert(compare, n, rest))
      case EQ() => Cons(m, rest)
    }
  }

  def isMember[A](compare : (A, A) => Order, n : A, lst : List[A]) : Boolean = lst match {
    case Nil() => false
    case Cons(m, rest) => compare(n, m) match {
      case LT() => false
      case GT() => isMember(compare, n, rest)
      case EQ() => true
    }
  }

}
