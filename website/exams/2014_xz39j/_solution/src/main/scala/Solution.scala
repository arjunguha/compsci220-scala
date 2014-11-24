

object Solution extends SolutionLike {

  val eps = 0.0001

  def ddx(f: Double => Double): Double => Double = (x: Double) => {
    (f(x + eps) - f(x)) / eps
  }


  def isMirrored[A](tree: BinTree[A]): Boolean = {
    def f[A](lhs: BinTree[A], rhs: BinTree[A]): Boolean = (lhs, rhs) match {
      case (Leaf(x), Leaf(y)) => x == y
      case (Node(lhs1, rhs1), Node(lhs2, rhs2)) => f(lhs1, rhs2) && f(rhs1, lhs2)
      case _ => false
    }
    tree match {
      case Leaf(x) => true
      case Node(lhs, rhs) => f(lhs, rhs)
    }
  }


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

  val notAbb = """b*(aa*b?)*""".r

  def isPrime(n: Int): Boolean = 2.to(n - 1).forall(m => n % m != 0)


}
