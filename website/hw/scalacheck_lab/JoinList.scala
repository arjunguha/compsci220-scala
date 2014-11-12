sealed trait JoinList[A]
case class Singleton[A](x: A) extends JoinList[A]
case class Empty[A]() extends JoinList[A]
case class Join[A](lhs: JoinList[A], rhs: JoinList[A]) extends JoinList[A]

object JoinList {

  def fromList[A](lst: List[A]) : JoinList[A] = lst match {
    case Nil => Empty()
    case List(x) => Singleton(x)
    case _ => {
      val len = lst.length / 2
      val lhs = lst.take(len)
      val rhs = lst.drop(len)
      join(fromList(lhs), fromList(rhs))
    }
  }

  def join[A](lhs: JoinList[A], rhs: JoinList[A]): JoinList[A] = (lhs, rhs) match {
    case (Empty(), _) => rhs
    case (_, Empty()) => lhs
    case _ => Join(lhs, lhs)
  }

  def toList[A](jl: JoinList[A]): List[A] = jl match {
    case Empty() => Nil
    case Singleton(x) => List(x)
    case Join(l, r) => toList(l) ++ toList(r)
  }

  def filter[A](pred: A => Boolean, jl: JoinList[A]): JoinList[A] = jl match {
    case Empty() => Empty()
    case Singleton(x) => if (pred(x)) Singleton(x) else Empty()
    case Join(l, r) => join(filter(pred, l), filter(pred, r))
  }

  def flatten[A](jl: JoinList[JoinList[A]]): JoinList[A] = jl match {
    case Empty() => Empty()
    case Singleton(x) => x
    case Join(l, r) => join(flatten(l), flatten(r))
  }

}
