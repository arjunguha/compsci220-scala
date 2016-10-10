import org.scalatest._
import org.scalatest.prop._
import org.scalacheck._
import JoinList._

class JoinListSuite extends FunSuite with GeneratorDrivenPropertyChecks {

  def isEven(n: Int): Boolean = n % 2 == 0

  test("filter for joinlists") {

    forAll { (lst: List[Int]) =>
      assert(toList(filter(isEven, fromList(lst))) == lst.filter(isEven))
    }
  }

  test("flatten for joinlists") {
    forAll { (lst: List[List[Int]]) =>
      val jl: JoinList[JoinList[Int]] = fromList(lst.map(fromList))
      assert (toList(flatten(jl)) == lst.flatten)
    }
  }

}
