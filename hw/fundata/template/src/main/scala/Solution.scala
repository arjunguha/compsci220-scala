object FunctionalDataStructures {

  case class Queue[A](front: List[A], back: List[A])

  def enqueue[A](elt: A, q: Queue[A]): Queue[A] = ???

  def dequeue[A](q: Queue[A]): Option[(A, Queue[A])] = ???

  sealed trait JoinList[A] {
    val size: Int
  }

  case class Empty[A]() extends JoinList[A] {
    val size = 0
  }

  case class Singleton[A](elt: A) extends JoinList[A] {
    val size = 1
  }

  case class Join[A](lst1: JoinList[A], lst2: JoinList[A], size: Int) extends JoinList[A]

  def max[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A] = ???

  def first[A](lst: JoinList[A]): Option[A] = ???

  def rest[A](lst: JoinList[A]): Option[JoinList[A]] = ???

  def nth[A](lst: JoinList[A], n: Int): Option[A] = ???

  def map[A,B](f: A => B, lst: JoinList[A]): JoinList[B] = ???

  def filter[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A] = ???

}