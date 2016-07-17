object HOF {

  def map2[A,B,C](f: (A, B) => C, lst1: List[A], lst2: List[B]): List[C] = ???
  def zip[A,B](lst1: List[A], lst2: List[B]): List[(A, B)] = ???
  def flatten[A](lst: List[List[A]]): List[A] = ???
  def flatten3[A](lst: List[List[List[A]]]): List[A] = ???
  def buildList[A](length: Int, f: Int => A): List[A] = ???
  def mapList[A, B](lst: List[A], f: A => List[B]): List[B] = 
  def partition[A](f: A => Boolean, lst: List[A]): (List[A], List[A]) = ???

}