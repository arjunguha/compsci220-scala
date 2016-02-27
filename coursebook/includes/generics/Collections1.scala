sealed trait LinkedList[+A] {
  def append[B >: A](other: LinkedList[B]): LinkedList[B] = this match {
    case EmptyList => other
    case Node(head, tail) => Node(head, tail.append(other))
  }

  def map[B](f: A => B): LinkedList[B] = this match {
    case EmptyList => EmptyList
    case Node(head, tail) => Node(f(head), tail.map(f))

  }
}

case object EmptyList extends LinkedList[Nothing]
case class Node[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

sealed trait JoinList[+A] {
  def append[B >: A](other: JoinList[B]): JoinList[B] = Join(this, other)

  def map[B](f: A => B): JoinList[B] = this match {
    case EmptyJoinList => EmptyJoinList
    case Singleton(x) => Singleton(f(x))
    case Join(lhs, rhs) => Join(lhs.map(f), rhs.map(f))
  }
}

case object EmptyJoinList extends JoinList[Nothing]
case class Join[A](lhs: JoinList[A], rhs: JoinList[A]) extends JoinList[A]
case class Singleton[A](value: A) extends JoinList[A]
