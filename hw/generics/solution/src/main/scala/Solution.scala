import hw.generics._

sealed trait BinTree[A] extends ListLike[A, BinTree[A]]

// NOTE: An alternative is to implement these methods in BinTree and use
// pattern-matching.
case class Node[A](lhs: BinTree[A], value: A, rhs: BinTree[A]) extends BinTree[A] {

  def cons(head: A): BinTree[A] = Node(Leaf(), head, this)

  def head(): Option[A] = lhs.head match {
    case None => Some(value)
    case Some(head) => Some(head)
  }

  def tail(): Option[BinTree[A]] = lhs.tail match {
    case None => Some(rhs)
    case Some(tail) => Some(Node(tail, value, rhs))
  }

  def isEmpty() = false
}

case class Leaf[A]() extends BinTree[A] {
  def cons(head: A): BinTree[A] = Node(Leaf(), head, Leaf())

  def head(): Option[A] = None

  def tail(): Option[BinTree[A]] = None

  def isEmpty() = false
}

object ListFunctions {

  // Super handy helper function. I do not expect students to come up with this.
  def listLikeMatch[E, C <: ListLike[E, C]](alist: C): Option[(E, C)] = {
    (alist.head(), alist.tail()) match {
      case (Some(hd), Some(tl)) => Some((hd, tl))
      case (None, None) => None
      case _ => throw new IllegalArgumentException("Badly defined ListLike")
    }
  }

  def filter[E, C <: ListLike[E, C]](f: E => Boolean, alist: C): C = {
    listLikeMatch[E, C](alist) match {
      case None => alist
      case Some((hd, tl)) => {
        if (f(hd)) {
          filter(f, tl).cons(hd)
        }
        else {
          filter(f, tl)
        }
      }
    }
  }

  def append[E, C <: ListLike[E, C]](alist1: C, alist2: C): C = {
    listLikeMatch[E, C](alist1) match {
      case None => alist2
      case Some((hd, tl)) => append[E, C](tl, alist2).cons(hd)
    }
  }


  def insert[E <: Ordered[E], C <: ListLike[E, C]](x: E, alist: C): C = {
    listLikeMatch[E, C](alist) match {
      case None => alist.cons(x)
      case Some((hd, tl)) => x.compare(hd) match {
        case LT => alist.cons(x)
        case _ => insert(x, tl).cons(hd)
      }
    }
  }

  def sort[A <: Ordered[A], C <: ListLike[A, C]](alist: C): C = {
    listLikeMatch[A, C](alist) match {
      case None => alist
      case Some((hd, tl)) => insert[A, C](hd, sort[A, C](tl))
    }
  }

}

// T1[X, Y]  Y = String due to h. Y = Int due to f. So, not possible
// T2[X, Y, Z, W]. X = Int by a. Y = Int by b. Z = String by c. W = String by d.
// Anything can extend T3 since it has a unique variable for each type in the
// trait.
class C1 extends T2[Int, Int, String, String]
  with T3[Int, Int, Int, String, String, String, Int] {
  def f(a: Int, b: Int): Int = 0
  def g(c: String): String = ""
  def h(d: String): Int = 0
}

// All types are the same, so it can implement any of the given interfaces
class C2 extends T1[Int, Int]
  with T2[Int, Int, Int, Int]
  with T3[Int, Int, Int, Int, Int, Int, Int] {
  def f(a: Int, b: Int): Int = 0
  def g(c: Int):  Int = 0
  def h(d: Int): Int = 0
}


// Cannot extend T1: a requires X = Int c requires x = A
// Cannot extend T2: b requires Y = A and result of f requires Y = Int
class C3[A](x: A) extends T3[Int, A, Int, A, String, String, A] {
  def f(a: Int, b: A): Int = 0
  def g(c: A): String = ""
  def h(d: String): A = x
}

// Cannot extend T1: result type constraints X = Int but all other constraints
// require X = C4[A]
// Cannot extend T2: result of h requires Y = Int but b requires Y = C4[A]
class C4[A](x: Int, y: C4[A]) extends T3[Int, C4[A], C4[A], Int, C4[A], C4[A], Int] {
  def f(a: Int, b: C4[A]): C4[A] = b
  def g(c: Int): C4[A] = y
  def h(d: C4[A]): Int = x
}




