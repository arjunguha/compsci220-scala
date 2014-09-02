package cmpsci220

/**
 * @group Binary Trees
 */
sealed abstract trait BinTree[A]

/**
 * @group Binary Trees
 */
case class Leaf[A]() extends BinTree[A]

/**
 * @group Binary Trees
 */
case class Node[A](left: BinTree[A], value: A, right: BinTree[A]) extends BinTree[A]