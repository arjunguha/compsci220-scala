import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck._

class FibTestSuite extends FunSuite with GeneratorDrivenPropertyChecks {

  def fib(n: Int): Int = {
    if (n == 0) 1
    else if (n == 1) 1
    else fib(n - 1) + fib(n - 2)
  }

  def optfib(n2: Int, n1: Int, n: Int, i: Int): Int = {
    info(s"optfib($n2, $n1, $n, $i)")
    if (i == 0) n else optfib(n1 ,n, n + n1, i - 1)
  }

  def altFib(n: Int): Int = {
    if (n <= 1) 1
    else optfib(1, 1, 2, n - 2)
  }

  test("altFib(n) = fib(n)") {
    forAll(Gen.choose(0, 20)) { n =>
      assert(altFib(n) == fib(n))
    }
  }

}
