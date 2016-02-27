trait ListLike[+T, +C <: ListLike[T, C]] {
  def isEmpty(): Boolean
  def head(): T
  def tail(): C
  def cons[
}


trait ListLike[+A, +Coll] {
  def cons[B :> A, Coll2 <: ListLike[B]](head: B): Coll2
  def append[B >: A, Coll2 <: ListLike[B]](other: Coll2): Coll2
}

sealed trait LinkedList[+A] extends LikeList[A, LinkedList[A]] {
  def cons[B :> A](head: B):
  def append[B >: A](other: ListLike[B]): ListLike[B] = this match {
    case EmptyList => other
    case Node(head, tail) => tail.append(other).cons(head)
  }
}

case object EmptyList extends LinkedList[Nothing]
case class Node[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

sealed trait JoinList[+A] extends ListLike[A, JoinList[A]] {

  def cons[B :> A](head: B): JoinList[B] = this match {
    case Singleton(x) => Join(Singleton(head), Singleton(x))
    case EmptyJoinList => Singleton(head)
    case Join(lhs, rhs) => Join(lhs.cons(head), rhs)
  }

  def append[B >: A](other: ListLike[B]): JoinList[B] = this match {
    case Singleton(x) => other.cons(x)
    case EmptyJoinList => other
    case Join(lhs, rhs) => lhs.append(rhs).append(other)
  }
}

case object EmptyJoinList extends JoinList[Nothing]
case class Join[A](lhs: JoinList[A], rhs: JoinList[A]) extends JoinList[A]
case class Singleton[A](value: A) extends JoinList[A]
