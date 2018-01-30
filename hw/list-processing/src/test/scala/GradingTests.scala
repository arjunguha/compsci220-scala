class GradingTests extends org.scalatest.FunSuite {
  import Lists._

  test("removeZeroes with only zeroes") {
    assert(removeZeroes(List(0, 0, 0, 0)) == Nil)
  }

  test("removeZeroes preserves non-zero values") {
    assert(removeZeroes(List(0, 1, 0, 2, 0)) == List(1, 2))
  }

  test("countEvens on an empty list") {
    assert(countEvens(Nil) == 0)
  }

  test("countEvens with no even numbers") {
    assert(countEvens(List(1, 3, 5, 7)) == 0)
  }

  test("countEvens with only even numbers") {
    assert(countEvens(List(1, 3, 2, 5, 7, 2, 9, 2)) == 3)
  }

  test("removeAlternating on an empty list") {
    assert(removeAlternating(Nil) == Nil)
  }

  test("removeAlternating on a singleton list (requires first in output)") {
    assert(removeAlternating("1" :: Nil) == "1" :: Nil)
  }

  test("removeAlternating on a long list (first or second in output)") {
    val result = removeAlternating(List("1","2","3","4","5"))
    assert(result == List("1","3","5") || result == List("2", "4"))
  }

  test("removeAlternating on a long list (requires first in output)") {
    assert(removeAlternating(List("1","2","3","4","5")) == List("1","3","5"))
  }

  test("isAscending on an empty list") {
    assert(isAscending(Nil) == true || isAscending(Nil) == false)
  }


  test("isAscending on a list with one element should produce true") {
    assert(isAscending(List(100)))
  }

  test("isAscending on an ascending list") {
    assert(isAscending(List(1, 3, 7, 100)) == true)
  }

  test("isAscending on a non-ascending list") {
    assert(isAscending(List(1, 3, 5, 2)) == false)
  }

  test("isAscending on a list with duplicates") {
    assert(isAscending(List(1, 1, 1, 1, 2)) == true)
  }

  test("addSub on an empty list") {
    assert(addSub(Nil) == 0)
  }

  test("addSub on a singleton list") {
    assert(addSub(List(10)) == 10)
  }

  test("addSub on a three-element list") {
    assert(addSub(List(10, 5, 1)) == 6)
  }

  test("partial credit: addSub  may subtract even positions") {
    val result = addSub(List(10, 5, 1))
    assert(result == -6 || result == 6)
  }

  test("fromTo(10, 11)") {
    assert(fromTo(10, 11) == List(10))
  }

  test("fromTo(5, 10)") {
    assert(fromTo(5, 10) == List(5, 6, 7, 8, 9))
  }

  test("for partial credit: fromTo works, but includes/excludes hi") {
    val result = fromTo(5, 10)
    assert(result == List(5, 6, 7, 8, 9) || result == List(5, 6, 7, 8, 9, 10))
  }

  test("for partial credit: fromTo works, but includes/excludes lo") {
    val result = fromTo(5, 10)
    assert(result.head == 5 || result.head == 6)
  }

  test("insertOrdered into empty list") {
    assert(insertOrdered(10, Nil) == List(10))
  }

  test("insertOrdered into head") {
    assert(insertOrdered(5, List(6, 7, 8)) == List(5,6,7,8))
  }

  test("insertOrdered into last position") {
    assert(insertOrdered(200, List(6, 7, 8)) == List(6,7,8,200))
  }

  test("insertOrdered into mid position") {
    assert(insertOrdered(7, List(6, 8)) == List(6,7,8))
  }

  test("sort an empty list") {
    assert(sort(Nil) == Nil)
  }

  test("sort non-empty list") {
    assert(sort(List(5, 4, 1, 2, 3)) == List(1, 2, 3, 4, 5))
  }

  test("sumDouble(Nil)") {
    assert(sumDouble(Nil) == 0)
  }

  test("sumDouble on a non-empty list") {
    assert(sumDouble(List(2, 2, 3, 7)) == 28)
  }
}