// Force students to write directly and not use builtin methods.
// Just look for method calls to penalize students.
object Solution {

  // Exercise 1
  def sumDouble(lst: List[Int]): Int = lst match {
    case Nil => 0
    case n :: rest => 2 * n + sumDouble(rest)
  }

  // Exercise 2
  def removeZeroes(lst: List[Int]): List[Int] = lst match {
    case Nil => Nil
    case 0 :: rest => removeZeroes(rest)
    case n :: rest => n :: removeZeroes(rest)
  }


  // Exercise 3
  def countEvens(lst : List[Int]): Int = lst match {
    case Nil => 0
    case n :: rest => if (n % 2 == 0) 1 + countEvens(rest) else countEvens(rest)
  }

  // Exercise 4
  def removeAlternating(lst: List[String]): List[String] = lst match {
    case Nil => Nil
    case _ :: Nil => Nil
    case _ :: str :: rest => str :: removeAlternating(rest)
  }

  // Exercise 5
  def isAscending(lst: List[Int]): Boolean = lst match {
    case Nil => true
    case m :: n :: rest => m <= n && isAscending(n :: rest)
    case _ :: Nil => false
  }

  // Exercise 6
  def addSub(lst: List[Int]): Int = lst match {
    case Nil => 0
    case x :: y :: z :: rest => x + y - z + addSub(rest)
    case x :: y :: Nil => x + y
    case x :: Nil => x
  }

  // Exercise 7
  def alternating(lst1: List[Int], lst2: List[Int]): List[Int] = lst1 match {
    case Nil => Nil
    case x :: rest1 => lst2 match {
      case Nil => Nil
      case y :: rest2 => x :: y :: intersperse(lst1, lst2)
    }
  }

  // Exercise 8
  def fromTo(lo: Int, hi: Int): List[Int] = {
    if (lo == hi) {
      lo :: Nil
    }
    else {
      lo :: fromTo(lo + 1, hi)
    }
  }

  // Exercise 9
  def insertOrdered(n: Int, lst: List[Int]): List[Int] = lst match {
    case Nil => n :: Nil
    case m :: rest => {
      if (n <= m) n :: m :: rest else m :: insertOrdered(n, rest)
    }
  }

  // Exercise 10
  def sort(lst: List[Int]): List[Int] = lst match {
    case Nil => Nil
    case n :: rest => insertOrdered(n, insertionSort(rest))
  }


}