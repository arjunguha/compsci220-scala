class Tests extends org.scalatest.FunSuite {
  import FunctionalDataStructures._

  def fromList[A](alist: List[A]): JoinList[A] = alist match {
    case Nil => Empty()
    case List(x) => Singleton(x)
    case _  => {
      val len = alist.length
      val (lhs, rhs) = lst.splitAt(len / 2)
      Join(fromList(lhs), fromList(rhs), len)
    }
  }

  def toList[A](alist: JoinList[A]): List[A] = lst match {
    case Empty() => Nil
    case Singleton(x) => List(x)
    case Join(alist1, alist2, _) => toList(alist1) ++ toList(alist2)
  }
}
