package cmpsci220.hw

package object recursion {

  private def reverseHelper[A](lst: List[A], out: List[A]): List[A] = lst match {
    case Empty() => out
    case Cons(head, tail) => reverseHelper(tail, Cons(head, out))
  }

  def reverse[A](lst: List[A]): List[A] = reverseHelper(lst, Empty())

  def map[A, B](f: A => B, lst: List[A]): List[B] = lst match {
    case Empty() => Empty()
    case Cons(head, tail) => Cons(f(head), map(f, tail))
  }

}