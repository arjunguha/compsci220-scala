

object Solution {

  def fringe[A](tree: BinTree[A]): Stream[A] = tree match {
    case Leaf(v) => Stream(v)
    case Node(lhs, rhs) => fringe(lhs) #::: fringe(rhs)
  }

  def sameFringe[A](tree1: Stream[A], tree2: Stream[A]) = {
    tree1.zip(tree2).forall { case (x, y) => x == y }
  }

  def filterIndex[A](pred: Int => Boolean, lst: List[A]): List[A] = {
    def f(n: Int, lst: List[A]): List[A] = lst match {
      case Nil => Nil
      case x :: rest => if (pred(n)) x :: f(n + 1, lst) else f(n + 1, lst)
    }

    f(0, lst)
  }

  def mapmap[A,B,C](f: A => B, g: B => C, lst: List[A]): List[C] = lst match {
    case Nil => Nil
    case a :: rest => g(f(a)) :: mapmap(f, g, rest)
  }

  val regex = """b*(aa*b?)*""".r

  def isPrime(n: Int): Boolean = 2.to(n - 1).forall(m => n % m != 0)


}
