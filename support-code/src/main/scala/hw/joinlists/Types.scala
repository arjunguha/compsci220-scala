package cmpsci220.hw.joinlists

sealed trait JoinList[A]
case class Empty[A]() extends JoinList[A]
case class Singleton[A](elt: A) extends JoinList[A]
case class Join[A](lst1: JoinList[A],
                   lst2: JoinList[A],
                   size: Int) extends JoinList[A]

/**
 * You need to implement these functions
 */
trait JoinListFunctions {

  def max[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A]

  def first[A](lst: JoinList[A]): Option[A]

  def rest[A](lst: JoinList[A]): Option[JoinList[A]]

  def nth[A](lst: JoinList[A], i: Int): Option[A]

  def map[A,B](f: A => B, lst: JoinList[A]): JoinList[B]

  def filter[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A]

}