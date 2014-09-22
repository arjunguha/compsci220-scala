package cmpsci220.hw

package object joinlists {

  def join[A](lst1: JoinList[A],
              lst2: JoinList[A]): JoinList[A] = (lst1, lst2) match {
    case (Empty(), _) => lst2
    case (_, Empty()) => lst1
    case _ => {
      val len1 = length(lst1)
      val len2 = length(lst2)
      val sumLen = len1 + len2
      val diffLen = len1 - len2
      if (math.abs(diffLen) <= 2) {
        Join(lst1, lst2, sumLen)
      }
      else {
        val (lst1balanced, lst2balanced) = splitAt(sumLen / 2, Join(lst1, lst2, sumLen))
        Join(lst1balanced, lst2balanced, sumLen)
      }
    }
  }

  def length[A](lst: JoinList[A]): Int = lst match {
    case Empty() => 0
    case Singleton(_) => 1
    case Join(_, _, n) => n
  }

  // Assumes n >= 0 and n < length(lst)
  def splitAt[A](n: Int,
                 lst: JoinList[A]): (JoinList[A], JoinList[A]) = lst match {
    case Empty() => (Empty(), Empty())
    case Singleton(x) => (Singleton(x), Empty())
    case Join(lst1, lst2, size) => {
      if (n < length(lst1)) {
        val (lst11, lst12) = splitAt(n, lst1)
        (lst11, join(lst12, lst2))
      }
      else {
        val (lst21, lst22) = splitAt(n - length(lst1), lst2)
        (join(lst1, lst21), lst22)
      }
    }
  }

  def fromList[A](lst: List[A]): JoinList[A] = lst match {
    case Nil => Empty()
    case x :: rest => join(Singleton(x), fromList(rest))
  }

  def toList[A](lst: JoinList[A]): List[A] = lst match {
    case Empty() => List()
    case Singleton(x) => List(x)
    case Join(lst1, lst2, _) => toList(lst1) ++ toList(lst2)
  }
}