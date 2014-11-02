import org.scalacheck._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class LectureSuite extends FunSuite with GeneratorDrivenPropertyChecks {

  test("concat distributes over map") {
    def incr(x: Int) = x + 1

    forAll { (lst1: List[Int], lst2: List[Int]) =>
      assert((lst1 ++ lst2).map(incr) == lst1.map(incr) ++ lst2.map(incr))
    }

  }

  test("reverse(reverse(lst))") {
    forAll { (lst: List[Int]) =>
      assert(lst.reverse.reverse == lst)
    }
  }

  test("fibonacci optimization") {
    def fib(n: Int): Int = {
      if (n == 0) {
        1
      }
      else if (n == 1) {
        1
      }
      else {
        fib(n - 1) + fib(n - 2)
      }
    }

    def fibLoop(n: Int): Int = {
      if (n <= 1) {
        return 1
      }
      var fib_n_minus_2 = 1
      var fib_n_minus_1 = 1
      var fib_n = fib_n_minus_2  + fib_n_minus_1
      for (i <- 2.until(n)) {
        fib_n_minus_1 = fib_n_minus_2
        fib_n_minus_2 = fib_n
        fib_n = fib_n_minus_2  + fib_n_minus_1
      }
      return fib_n
    }

    forAll { (n: Int) =>
      assert(fibLoop(n) == fib(n))
    }

  }



}
