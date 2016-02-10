object FunctionalDataStructures {

  //
  // Part 1. Persistent Queues
  //

  def enqueue[A](elt: A, q: Queue[A]): Queue[A] = ???

  def dequeue[A](q: Queue[A]): Option[(A, Queue[A])] = ???

  //
  // Part 2. Join Lists
  //

  def max[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A] = ???

  def first[A](lst: JoinList[A]): Option[A] = ???

  def rest[A](lst: JoinList[A]): Option[JoinList[A]] = ???

  def nth[A](lst: JoinList[A], n: Int): Option[A] = ???

  def map[A,B](f: A => B, lst: JoinList[A]): JoinList[B] = ???

  def filter[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A] = ???

}