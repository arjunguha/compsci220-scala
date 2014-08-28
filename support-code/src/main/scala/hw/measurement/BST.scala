package cmpsci220.hw.measurement

import cmpsci220._

object BST {

  def insert[A](compare : (A, A) => Order, n : A, tree : BinTree[A]) : BinTree[A] = tree match {
    case Leaf() => Node(Leaf(), n, Leaf())
    case Node(left, m, right) => compare(n, m) match {
      case LT() => Node(insert(compare, n, left), m, right)
      case GT() => Node(left, m, insert(compare, n, right))
      case EQ() => Node(left, m, right)
    }
  }

  def isMember[A](compare : (A, A) => Order, n : A, tree : BinTree[A]) : Boolean = tree match {
    case Leaf() => false
    case Node(left, m, right) => compare(n, m) match {
      case LT() => isMember(compare, n, left)
      case GT() => isMember(compare, n, right)
      case EQ() => true
    }
  }

}
