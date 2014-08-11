package hw.measurement

sealed trait Tree[A]
case class Leaf[A]() extends Tree[A]
case class Node[A](left : Tree[A], elt : A, right : Tree[A]) extends Tree[A]

object Tree {

  def empty[A] : Leaf[A] = Leaf()

  def insert[A](compare : (A, A) => Order, n : A, tree : Tree[A]) : Tree[A] = tree match {
    case Leaf() => Node(Leaf(), n, Leaf())
    case Node(left, m, right) => compare(n, m) match {
      case LT() => Node(insert(compare, n, left), m, right)
      case GT() => Node(left, m, insert(compare, n, right))
      case EQ() => Node(left, m, right)
    }
  }

  def isMember[A](compare : (A, A) => Order, n : A, tree : Tree[A]) : Boolean = tree match {
    case Leaf() => false
    case Node(left, m, right) => compare(n, m) match {
      case LT() => isMember(compare, n, left)
      case GT() => isMember(compare, n, right)
      case EQ() => true
    }
  }

}
