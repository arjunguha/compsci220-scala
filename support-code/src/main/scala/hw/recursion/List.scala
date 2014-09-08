package cmpsci220.hw.recursion

/**
 * @group List
 */
sealed abstract trait List[A] {

  override def toString() : String = {

    val builder = new collection.mutable.StringBuilder()
    builder ++= "List("

    def loop(lst : List[A]) : Unit = lst match {
      case Empty() => {
        builder ++= ")"
      }
      case Cons(a,Empty()) => {
        builder ++= a.toString
        builder ++= ")"
      }
      case Cons(a, rest) => {
        builder ++= a.toString
        builder ++= ", "
        loop(rest)
      }
    }

    loop(this)
    builder.toString
  }

}

/**
 * @group List
 */
case class Empty[A]() extends List[A]

/**
 * @group List
 */
case class Cons[A](head : A, tail : List[A]) extends List[A]

/**
 * @group List
 */
object List {

  def apply[A](items : A*) : List[A] = {
    items.foldRight(Empty[A]() : List[A]) { (item, lst) => Cons[A](item, lst) }
  }

  private def reverseHelper[A](lst: List[A], out: List[A]): List[A] = lst match {
    case Empty() => out
    case Cons(head, tail) => reverseHelper(lst, Cons(head, out))
  }

  def reverse[A](lst: List[A]): List[A] = reverseHelper(lst, Empty())

  def map[A, B](f: A => B, lst: List[A]): List[B] = lst match {
    case Empty() => Empty()
    case Cons(head, tail) => Cons(f(head), map(f, tail))
  }

}
