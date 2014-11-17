sealed trait BinTree[+A]
case class Node[A](lhs: BinTree[A], rhs: BinTree[A]) extends BinTree[A]
case class Leaf[A](value: A) extends BinTree[A]

trait SolutionLike {

  def fringe[A](tree: BinTree[A]): Stream[A]
  def sameFringe[A](fringe1: Stream[A], fringe2: Stream[A]): Boolean
  def filterIndex[A](pred: Int => Boolean, lst: List[A]): List[A]
  def mapmap[A,B,C](f: A => B, g: B => C, lst: List[A]): List[C]
  val filterIndex(n => n < 2, List("a", "b", "c", "d")` produces `List("a", "b")`: util.matching.Regex
  def isPrime(n: Int): Boolean
  val primes: Stream[Int]
}