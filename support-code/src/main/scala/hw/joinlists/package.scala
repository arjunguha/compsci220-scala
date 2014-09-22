package cmpsci220.hw

package object joinlists {

  def append[A](lst1: JoinList[A], lst2: JoinList[A]): JoinList[A] =
    (lst1, lst2) match {
      case (Empty(), _) => lst2
      case (_, Empty()) => lst1
      case _ => Append(lst1, lst2)
  }



}