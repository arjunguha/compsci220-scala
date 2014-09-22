import cmpsci220.hw.joinlists._

object Solution extends JoinListFunctions {

  def max[A](lst: JoinList[A],
             compare: (A, A) => Boolean): Option[A] = lst match {
    case Empty() => None
    case Singleton(elt) => Some(elt)
    case Join(lst1, lst2, _) => (max(lst1, compare), max(lst2, compare)) match {
      case (Some(x), Some(y)) => if (compare(x,y)) Some(x) else Some(y)
      case (Some(x), None) => Some(x)
      case (None, Some(y)) => Some(y)
      case (None, None) => None
    }
  }

  def first[A](lst: JoinList[A]): Option[A] = lst match {
    case Empty() => None
    case Singleton(x) => Some(x)
    case Join(lst1, _, _) => first(lst1)
  }

  def rest[A](lst: JoinList[A]): Option[JoinList[A]] = lst match {
    case Empty() => None
    case Singleton(x) => None
    case Join(lst1, lst2, _) => rest(lst1) match {
      case None => rest(lst2)
      case Some(lst1rest) => Some(join(lst1rest, lst2))
    }
  }

  def nth[A](lst: JoinList[A], n: Int): Option[A] = lst match {
    case Singleton(x) => {
      if (n == 0) {
        Some(x)
      }
      else {
        None
      }
    }
    case Join(lst1, lst2, size) => {
      if (n >= 0 && n < length(lst1)) {
        nth(lst1, n)
      }
      else if (n < length(lst2)) {
        nth(lst2, n - length(lst1))
      }
      else {
        None
      }
    }
    case _ => None
  }

  def map[A,B](f: A => B, lst: JoinList[A]): JoinList[B] = lst match {
    case Empty() => Empty()
    case Singleton(x) => Singleton(f(x))
    case Join(lst1, lst2, size) => Join(map(f, lst1), map(f, lst2), size)
  }

  def filter[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A] = lst match {
    case Empty() => Empty()
    case Singleton(x) => if (pred(x)) { Singleton(x) } else { Empty() }
    case Join(lst1, lst2, _) => join(filter(pred, lst1), filter(pred, lst2))
  }

  // import scala.concurrent._

  // def parMap[A, B](f: A => B, lst: JoinList[A])
  //   (implicit ec: ExecutionContext): Future[JoinList[B]] = lst match {
  //   case Empty() => Future(Empty())
  //   case Singleton(x) => Future(Singleton(f(x)))
  //   case Join(lst1, lst2, size) =>
  //     parMap(f, lst1).zip(parMap(f, lst2)).map { case (lhs, rhs) => Join(lhs, rhs, size) }
  // }

}