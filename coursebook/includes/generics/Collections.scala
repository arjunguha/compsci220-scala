
sealed trait ListLike[+A, ] {
  def cons[B >: A](head: B, tail: List[B]): List[B]
  def fold[B](init: B, f: (A, B) => B): B

  def append[B >: A](other: ListLike[B]): List[B] = {
    this.fold(other, (x, acc: LinkedList[B]) => Node(x, acc))
  }
}

sealed trait LinkedList[+A] {



  def map[B](f: A => B): LinkedList[B] = {
    this.fold(EmptyList, (x, acc: LinkedList[B]) => Node(f(x), acc))
  }

  def fold[B](init: B, f: (A, B) => B): B
}

case object EmptyList extends LinkedList[Nothing] {
  def fold[B](init: B, f: (Nothing, B) => B) = init
}

case class Node[A](head: A, tail: LinkedList[A]) extends LinkedList[A] {
  def fold[B](init: B, f: (A, B) => B) = f(head, tail.fold(init, f))
}

sealed trait JoinList[+A] {

}

case object EmptyJoinList extends JoinList[Nothing] {

}

case class Join[A](lhs: JoinList[A], rhs: JoinList[A]) extends JoinList[A]

case class Singleton[A](value: A) extends JoinList[A]
