object FunctionalDataStructures {


  //
  // Part 1. Persistent Queues
  //

  def enqueue[A](elt: A, q: Queue[A]): Queue[A] = Queue(q.front, elt :: q.back)

  def dequeue[A](q: Queue[A]): Option[(A, Queue[A])] = q match {
    case Queue(head :: tail, back) => Some((head, Queue(tail, back)))
    case Queue(Nil, back) => back.reverse match {
      case Nil => None
      case head :: tail => Some((head, Queue(tail, Nil)))
    }
  }

  //
  // Part 2. Join Lists
  //

  def max[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A] = lst match {
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
    case Join(lst1, lst2, size) => rest(lst1) match {
      case None => rest(lst2)
      case Some(lst1rest) => Some(Join(lst1rest, lst2, size - 1))
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
      if (n >= 0 && n < lst1.size) {
        nth(lst1, n)
      }
      else if (n < lst2.size) {
        nth(lst2, n - lst2.size)
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
    case Join(lst1, lst2, size) => {
      val lst1Filtered = filter(pred, lst1)
      val lst2Filtered = filter(pred, lst2)
      Join(lst1Filtered, lst2Filtered, lst1Filtered.size + lst2Filtered.size)
    }
  }

}