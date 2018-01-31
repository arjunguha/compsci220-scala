object HOF {

  def map2[A,B,C](f: (A, B) => C, lst1: List[A], lst2: List[B]): List[C] = {
    lst1.zip(lst2).map({ case (x,y) => f(x, y) })
  }

  def zip[A,B](lst1: List[A], lst2: List[B]): List[(A, B)] = lst1.zip(lst2)

  def flatten[A](lst: List[List[A]]): List[A] = lst.flatten

  def flatten3[A](lst: List[List[List[A]]]): List[A] = lst.flatten.flatten

  def buildList[A](length: Int, f: Int => A): List[A] = {
    0.until(length).map(f).toList
  }

  def mapList[A, B](lst: List[A], f: A => List[B]): List[B] = lst.flatMap(f)

  def partition[A](f: A => Boolean, lst: List[A]): (List[A], List[A]) = {
    lst.partition(f)
  }

  def merge[A](lessThan: (A, A) => Boolean, alist1: List[A],
               alist2: List[A]): List[A] = (alist1, alist2) match {
    case (Nil, Nil) => Nil
    case (x :: xs, y :: ys) =>
      if (lessThan(x, y)) x :: merge(lessThan, xs, y :: ys)
      else y :: merge(lessThan, x :: xs, ys)
    case (Nil, ys) => ys
    case (xs, Nil) => xs
  }

  def sort[A](lessThan: (A, A) => Boolean, alist: List[A]): List[A] = {
    alist.sortWith(lessThan)
  }

}