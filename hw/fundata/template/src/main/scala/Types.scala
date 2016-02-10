// Do not change this file

case class Queue[A](front: List[A], back: List[A])

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



