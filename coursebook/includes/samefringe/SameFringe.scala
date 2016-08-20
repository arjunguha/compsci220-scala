sealed trait BinTree[+A]
case class Node[A](lhs: BinTree[A], rhs: BinTree[A]) extends BinTree[A]
case class Leaf[A](x: A) extends BinTree[A]

trait Generator[T] {
  def gen: Option[(T, Generator[T])]

  def ++(other: => Generator[T]): Generator[T] = new Cat(this, other)
}


class Cat[T](gen1: => Generator[T], gen2: => Generator[T]) extends Generator[T] {

  def gen: Option[(T, Generator[T])] = gen1.gen match {
    case None => gen2.gen
    case Some ((x, gen1Rest)) => Some((x, new Cat(gen1Rest, gen2)))
  }
}

class One[T](x: T) extends Generator[T] {
  def gen = Some((x, new Zero[T]()))
}

class Zero[T]() extends Generator[T] {
  def gen = None
}


object Generator {
  def one[T](x: T) = new One(x)
}

def fringe[T](t: BinTree[T]): Generator[T] = t match {
  case Leaf(x) => {
    println(x)
    Generator.one(x)
    }
  case Node(lhs, rhs) => fringe(lhs) ++ fringe(rhs)
}

def sameFringeRec[T](t1: Generator[T], t2: Generator[T]): Boolean = (t1.gen, t2.gen) match {
  case (None, None) => true
  case (Some((x, t1Rest)), Some((y, t2Rest))) => x == y && sameFringeRec(t1Rest, t2Rest)
  case _ => false
}

def sameFringe[T](t1: BinTree[T], t2: BinTree[T]) = sameFringeRec(fringe(t1), fringe(t2))

val t1 = Node(Leaf(10), Node(Leaf(20), Leaf(40)))
val t2 = Node(Node(Leaf(10), Leaf(5)), Leaf(40))


println(sameFringe(t1, t2))
