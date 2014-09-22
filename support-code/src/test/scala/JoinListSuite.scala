import cmpsci220.hw.joinlists._
import org.scalatest.FunSuite

class JoinListSuite extends FunSuite {


  def isSizeBalanced[A](lst: JoinList[A]): Boolean = lst match {
    case Empty() => true
    case Singleton(_) => true
    case Join(lst1, lst2, size) =>
      isSizeBalanced(lst1) && isSizeBalanced(lst2) &&
      math.abs(length(lst1) - length(lst2)) <= 2
    }

  test("join is smart about balancing") {

    for (i <- 0.to(20)) {
      val lst = 0.to(i).toList
      val jl = fromList(lst)
      assert(isSizeBalanced(jl), s"for $i and $jl")
      assert(toList(jl) == lst)
    }

  }


}


