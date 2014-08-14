package cmpsci220

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

case class Empty[A]() extends List[A]
case class Cons[A](head : A, tail : List[A]) extends List[A]

object List {

  def apply[A](items : A*) : List[A] = {
    items.foldRight(Empty[A]() : List[A]) { (item, lst) => Cons[A](item, lst) }
  }

}

