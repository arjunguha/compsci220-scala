package cmpsci220.hw

package object joinlists {

  def smartAppend[A](lst1: JoinList[A], lst2: JoinList[A]): JoinList[A] =
    (lst1, lst2) match {
      case (EmptyJL(), _) => lst2
      case (_, EmptyJL()) => lst1
      case _ => AppendJL(lst1, lst2)
  }



}