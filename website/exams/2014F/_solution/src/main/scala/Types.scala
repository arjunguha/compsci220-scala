sealed trait BinTree[+A]
case class Node[A](lhs: BinTree[A], rhs: BinTree[A]) extends BinTree[A]
case class Leaf[A](value: A) extends BinTree[A]

trait SolutionLike {
  def ddx(f: Double => Double): Double => Double
  def isMirrored[A](tree: BinTree[A]): Boolean
  def fringe[A](tree: BinTree[A]): Stream[A]
  def sameFringe[A](fringe1: Stream[A], fringe2: Stream[A]): Boolean
  def filterIndex[A](pred: Int => Boolean, lst: List[A]): List[A]
  def mapmap[A,B,C](f: A => B, g: B => C, lst: List[A]): List[C]
  val notAbb: util.matching.Regex
}
