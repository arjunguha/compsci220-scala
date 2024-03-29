object HOF {

  def map2[A,B,C](f: (A, B) => C, lst1: List[A], lst2: List[B]): List[C] =  {
    (lst1, lst2) match {
      case (Nil, Nil) => Nil
      case (h1 :: t1, h2 :: t2) => f(h1, h2) :: map2(f, t1, t2)
      case _ => ???
    }
  }

  def zip[A,B](lst1: List[A], lst2: List[B]): List[(A, B)] = {
    map2((x: A, y: B) => (x, y), lst1, lst2)
  }

  // Helper
  def append[A](lst1: List[A], lst2: List[A]): List[A] = lst1 match {
    case Nil => lst2
    case h :: tl => h :: append(tl, lst2)
  }

  def flatten[A](lst: List[List[A]]): List[A] =  lst match {
    case Nil => Nil
    case h :: t => append(h, flatten(t))
  }

  // Helper (from class)
  def map[A,B](f: A => B, lst: List[A]): List[B] = lst match {
    case Nil => Nil
    case h :: t => f(h) :: map(f, t)
  }

  def flatten3[A](lst: List[List[List[A]]]): List[A] = {
    // Scala seems unable to infer the type arguments. :(
    flatten(map[List[List[A]], List[A]](flatten, lst))
  }

  def buildList[A](length: Int, f: Int => A): List[A] = {
    def loop(i: Int): List[A] = {
      if (i == length) {
        Nil
      }
      else {
        f(i) :: loop(i + 1)
      }
    }
    loop(0)
  }

  def mapList[A, B](lst: List[A], f: A => List[B]): List[B] =  {
    flatten(map(f, lst))
  }

  // Helper (from class)
  def filter[A](f: A => Boolean, lst: List[A]): List[A] = lst match {
    case Nil => Nil
    case h :: t => if (f(h)) h :: filter(f, t) else filter(f, t)
  }

  def partition[A](f: A => Boolean, lst: List[A]): (List[A], List[A]) = {
    (filter(f, lst), filter((x: A) => !f(x), lst))
  }

  def merge[A](lessThan: (A, A) => Boolean, alist1: List[A], alist2: List[A]): List[A] = (alist1, alist2) match {
    case (_, Nil) => alist1
    case (Nil, _) => alist2
    case (x :: xs, y :: ys) => if (lessThan(x, y)) x :: merge(lessThan, xs, y :: ys) else y :: merge(lessThan, x :: xs, ys)
  }

  def length[A](alist: List[A]): Int = alist match {
    case Nil => 0
    case _ :: tl => 1 + length(tl)
  }

  def splitAt[A](n: Int, alist: List[A]): (List[A], List[A]) = {
    (n, alist) match {
      case (0, _) => (Nil, alist)
      case (n, Nil) => (Nil, Nil)
      case (n, hd :: tl) => {
        val (prefix, suffix) = splitAt(n - 1, tl)
        (hd :: prefix, suffix)
      }
    }
  }

  def sort[A](lessThan: (A, A) => Boolean, alist: List[A]): List[A] = alist match {
    case Nil => Nil
    case x :: Nil => List(x)
    case _ :: _ => {
      val (prefix, suffix) = splitAt(length(alist) / 2, alist)
      merge(lessThan, sort(lessThan, prefix), sort(lessThan, suffix))
    }
  }

}