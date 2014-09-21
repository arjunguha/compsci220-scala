package cmpsci220.hw.joinlists

sealed trait JoinList[A]
case class EmptyJL[A]() extends JoinList[A]
case class SingletonJL[A](elt: A) extends JoinList[A]
case class AppendJL[A](lst1: JoinList[A], lst2: JoinList[A]) extends JoinList[A]

