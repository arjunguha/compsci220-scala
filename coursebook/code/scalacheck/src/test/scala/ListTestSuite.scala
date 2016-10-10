import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck._

class ListTestSuite extends FunSuite with GeneratorDrivenPropertyChecks {

  def split(sep: Char, astring: String): List[String] = {
    def splitRec(chars: List[Char]): List[String] = {
      val prefix = chars.takeWhile(ch => ch != sep).mkString
      chars.dropWhile(ch => ch != sep) match {
        case Nil => Nil
        case _ :: suffix => prefix :: splitRec(suffix)
      }
    }
    splitRec(astring.toList)
  }

  def join(sep: Char, strings: List[String]): String = strings match {
    case Nil => ""
    case List(x) => x
    case x1 :: x2 :: xs => x1 + sep + join(sep, x2 :: xs)
  }

  test("reverse-reverse") {
    forAll { (alist: List[Int]) =>
      assert(alist.reverse.reverse == alist)
    }
  }

  test("reverse concat") {
    forAll { (alist1: List[Int], alist2: List[Int]) =>
      assert((alist1 ++ alist2).reverse == alist2.reverse ++ alist1.reverse)
    }
  }

  test("concat distributes over map") {
    def incr(x: Int) = x + 1

    forAll { (lst1: List[Int], lst2: List[Int]) =>
      assert((lst1 ++ lst2).map(incr) == lst1.map(incr) ++ lst2.map(incr))
    }
  }

  def qsort(lst: List[Int]): List[Int] = {
    lst match {
      case Nil => Nil
      case pivot :: rest => {
        val lhs = rest.filter(_ < pivot)
        val rhs = rest.filter(_ >= pivot)
        qsort(lhs) ++ List(pivot) ++ qsort(rhs)
      }
    }
  }

  def isSorted(lst: List[Int]): Boolean = lst match {
    case Nil => true
    case List(x) => true
    case x :: y:: rest => x <= y && isSorted(y :: rest)
  }

  test("qsort checking") {
    forAll { (lst: List[Int]) =>
      val sortedList = qsort(lst)
      assert(isSorted(sortedList))
      assert(sortedList.length == lst.length)
    }
  }

}
