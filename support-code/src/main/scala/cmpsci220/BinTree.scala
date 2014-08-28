package cmpsci220

sealed abstract trait BinTree[A]

case class Leaf[A]() extends BinTree[A]
case class Node[A](left: BinTree[A], value: A, right: BinTree[A]) extends BinTree[A]