class Tests extends org.scalatest.FunSuite {
  import FunctionalDataStructures._

  def fromList[A](lst: List[A]): JoinList[A] = lst match {
    case Nil => Empty()
    case List(x) => Singleton(x)
    case _  => {
      val len = lst.length
      val (lhs, rhs) = lst.splitAt(len / 2)
      Join(fromList(lhs), fromList(rhs), len)
    }
  }

  def toList[A](lst: JoinList[A]): List[A] = lst match {
    case Empty() => Nil
    case Singleton(x) => List(x)
    case Join(lst1, lst2, _) => toList(lst1) ++ toList(lst2)
  }
}