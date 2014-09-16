package cmpsci220.hw

import cmpsci220._
import org.apache.commons.math3.stat.regression.SimpleRegression

package object measurement {

  val emptyOrdList: OrdList = OrdListImpl(Empty())

  private def mapOrdList[A, B](f : List[Int] => B, lst: OrdList): B = lst match {
    case OrdListImpl(lst) => f(lst)
  }

  def insertOrdList(value: Int, lst: OrdList): OrdList = {

    def ins(lst: List[Int]): List[Int] = lst match {
      case Empty() => List(value)
      case Cons(v, tail) => {
        if (value < v) {
          Cons(value, lst)
        }
        else if (value == v) {
          lst
        }
        else {
          Cons(v, ins(tail))
        }
      }
    }

    OrdListImpl(mapOrdList(ins, lst))
  }

  def isMemberOrdList(value: Int, lst: OrdList): Boolean = {

    def lookup(lst: List[Int]): Boolean = lst match {
      case Empty() => false
      case Cons(v, tail) => {
        if (value < v) {
          false
        }
        else if (value == v) {
          true
        }
        else {
          lookup(tail)
        }
      }
    }

    mapOrdList(lookup, lst)

  }

  val emptyBST: BST = BSTLeaf()

  def insertBST(value: Int, bst: BST): BST = bst match {
    case BSTLeaf() => BSTNode(BSTLeaf(), value, BSTLeaf())
    case BSTNode(lhs, v, rhs) => {
      if (value < v) {
        BSTNode(insertBST(value, lhs), v, rhs)
      }
      else if (value > v) {
        BSTNode(lhs, v, insertBST(value, rhs))
      }
      else {
        BSTNode(lhs, value, rhs)
      }
    }
  }

  def isMemberBST(value: Int, bst: BST): Boolean = bst match {
    case BSTLeaf() => false
    case BSTNode(lhs, v, rhs) => {
      if (value < v) {
        isMemberBST(value, lhs)
      }
      else if (value > v) {
        isMemberBST(value, rhs)
      }
      else {
        true
      }
    }
  }

  val emptyAVL: AVL = AVLLeaf()

  private def balance(p: AVL): AVL = p match {
    case AVL(AVL(r, q, 1, b), p, 2, c) => {
      // left-left case
      AVL(r, q, AVL(b, p, c))
    }
    case AVL(AVL(AVL(a1, r, _, a2), q, -1, b), p, 2, c) => {
      // left-right case
      AVL(AVL(a1, r, a2), q, AVL(b, p, c))
    }
    case AVL(c, p, -2, AVL(b, q, -1, r)) => {
      // right-right case
      AVL(AVL(c, p, b), q, r)
    }
    case AVL(c, p, -2, AVL(AVL(b1, r, _, b2), q, 1, a)) => {
      // right-left case
      AVL(AVL(c, p, b1), r, AVL(b2, q, a))
    }
    case _ => p
  }

  def insertAVL(value: Int, avl: AVL): AVL = avl match {
    case AVLLeaf() => AVLNode(AVLLeaf(), value, AVLLeaf())
    case AVLNode(lhs, v, rhs) => {
      if (value < v) {
        val newLhs = insertAVL(value, lhs)
        balance(AVLNode(newLhs, v, rhs))
      }
      else if (value > v) {
        val newRhs = insertAVL(value, rhs)
        balance(AVLNode(lhs, v, newRhs))
      }
      else {
        avl
      }
    }
  }

  def isMemberAVL(value: Int, avl: AVL): Boolean = avl match {
    case AVLLeaf() => false
    case AVLNode(lhs, v, rhs) => {
      if (value < v) {
        isMemberAVL(value, lhs)
      }
      else if (value > v) {
        isMemberAVL(value, rhs)
      }
      else {
        true
      }
    }
  }

  /**
   * Given a list of pairs that represent (x, y) coordinates, tries to fit
   * the points to a line.
   *
   * The function returns three values: (m, b, rRq)
   * The line is defined by y = m * x + b and rRq measures the error.
   * Intuitively, if rSq is 1, then the line perfectly fits the data. Lower
   * values indicate worse fits.
   */
  def linearRegression(points: List[(Int, Double)]): (Double, Double, Double) = {
    val reg = new SimpleRegression()
    for ((x, y) <- List.toScalaList(points)) {
      reg.addData(x, y)
    }
    (reg.getSlope, reg.getIntercept, reg.getRSquare)
  }

  /**
   * Given a list of pairs that represent (x, y) coordinates, tries to fit
   * to a line, where the x coordinates are on a semilog axis.
   *
   * The function returns three values: (m, b, rRq)
   * The line is defined by y = m * ln(x) + b and rRq measures the error.
   * Intuitively, if rSq is 1, then the line perfectly fits the data. Lower
   * values indicate worse fits.
   */
  def logRegression(points: List[(Int, Double)]): (Double, Double, Double) = {
    val reg = new SimpleRegression()
    for ((x, y) <- List.toScalaList(points)) {
      reg.addData(math.log(x), y)
    }
    (reg.getSlope, reg.getIntercept, reg.getRSquare)
  }

}