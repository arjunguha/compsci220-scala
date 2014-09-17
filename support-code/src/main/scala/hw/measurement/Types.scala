package cmpsci220.hw.measurement

import cmpsci220._

sealed trait OrdList
private case class OrdListImpl(list: List[Int]) extends OrdList

sealed trait BST
private case class BSTLeaf() extends BST
private case class BSTNode(lhs: BST, value: Int, rhs: BST) extends BST

sealed trait AVL {

  private[measurement] val height: Int

  private[measurement] def printDepth(offset: Int): String

  override def toString() = printDepth(0)


}

sealed case class LinearRegressionResult(slope: Double, intercept: Double, rSquared: Double)

private case class AVLLeaf() extends AVL {

  val height: Int = 0

  def printDepth(offset: Int): String = {
    val prefix = if (offset > 0) {
      ("| " * (offset - 1)) + "+-"
    }
    else {
      ""
    }
    prefix + ".\n"
  }

}

private case class AVLNode(lhs: AVL,
                           value: Int,
                           rhs: AVL) extends AVL {

  val height: Int = 1 + math.max(lhs.height, rhs.height)

  def printDepth(offset: Int): String = {
    val prefix = if (offset > 0) {
      ("| " * (offset - 1)) + "+-"
    }
    else {
      ""
    }
    s"$prefix$value\n" + lhs.printDepth(offset + 1) + rhs.printDepth(offset + 1)
  }

}

private object AVL {

  import scala.{Some, None, Option}

  // Extrator that matches a non-empty tree and produces
  // (lhs, key-value pair, balance factor, rhs)
  def unapply(avl: AVL): Option[(AVL, Int, Int, AVL)] = avl match {
    case AVLLeaf() => None
    case AVLNode(lhs, value, rhs) => Some((lhs, value, lhs.height - rhs.height, rhs))
  }

  // Constructor that builds a non-empty tree from the lhs, key-value pair,
  // and rhs. It calculates the height and ensures that the balance factor
  // is {-1, 0, +1}.
  def apply(lhs: AVL, value: Int, rhs: AVL, msg: String): AVL = {
    require(math.abs(lhs.height - rhs.height) <= 1, msg)
    AVLNode(lhs, value, rhs)
  }

}

