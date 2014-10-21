package lectures.searching

object Searching {

  def substring(sub: String, str: String) = listContains(sub.toList, str.toList)

  def listContains[A](sublst: List[A], lst: List[A]): Boolean = {
    (sublst, lst) match {
      case (Nil, _) => true
      case (x :: sublst1, y :: lst1) => {
        if (x == y) {
          listContains(sublst1, lst1) || listContains(x :: sublst1, lst1)
        }
        else {
          listContains(x :: sublst1, lst1)
        }
      }
      case (_ :: _, Nil) => false
    }
  }

  sealed trait Tree[+A]
  case object Leaf extends Tree[Nothing]
  case class Node[A](lhs: Tree[A], value: A, rhs: Tree[A]) extends Tree[A]

  def subPath[A](path: List[A], tree: Tree[A]): Boolean = {
    (path, tree) match {
      case (Nil, _) => true
      case (_ :: _, Leaf) => false
      case (x :: rest, Node(lhs, y, rhs)) => {
        (x == y && subPath(rest, lhs)) ||
        (x == y && subPath(rest, rhs)) ||
        subPath(x :: rest, lhs) ||
        subPath(x :: rest, rhs)
      }
    }
  }

  case class RoseTree[A](children: Map[A, RoseTree[A]])

  def subPath[A](path: List[A], tree: RoseTree[A]): Boolean = {
    path match {
      case Nil => true
      case head :: tail => tree.children.get(head) match {
        case None => tree.children.exists { case (_, t) => subPath(path, t) }
        case Some(t) => {
          subPath(tail, t) ||
          tree.children.exists { case (_, t) => subPath(path, t) }
        }
      }
    }
  }

}

