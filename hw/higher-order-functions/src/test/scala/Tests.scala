import HOF._

// NOTE: The test cases for sort and merge support lessThan and !lessThan
class TestSuite extends org.scalatest.FunSuite {

  case class A(x: Int)
  case class B(y: Int)
  case class C(z: Int)

  def combine(x: A, y: B): C = C(x.x + y.y)
  def aLT(x: A, y: A): Boolean = x.x < y.x

  test("Does map2 work on empty lists?") {
    assert(map2(combine, Nil, Nil) == Nil)
  }

  test("Does map2 work on non-empty lists?") {
    assert(map2(combine, List(A(10), A(20), A(30)), List(B(1), B(2), B(3))) == List(C(11), C(22), C(33)))
  }

  test("Does zip work on empty lists?") {
    assert(zip[A, B](Nil, Nil) == Nil)
  }

  test("Does zip work on non-empty lists?") {
    assert(zip(List(A(10), A(20)), List(B(50), B(90))) ==
           List((A(10), B(50)), (A(20), B(90))))
  }

  test("Does flatten work on empty lists?") {
    assert(flatten(Nil) == Nil)
  }

  test("Does flatten work when there are empty lists in the input?") {
    assert(flatten(List(List(1), Nil, List(2))) == List(1, 2))
  }

  test("Does flatten work when all sub-lists are non-empty?") {
    assert(flatten(List(List(1,2), List(4, 5), List(10, 11))) ==
           List(1,2,4,5,10,11))
  }

  test("Does flatten3 work on an empty list?") {
    assert(flatten3(Nil) == Nil)
  }

  test("Does flatten3 work on a non-empty list?") {
    assert(flatten3(List(List(List(1,2,3), List(4,5,6)), List(List(7, 8, 9)))) 
           == List(1,2,3,4,5,6,7,8,9))
  }

  test("When n = 0, buildList should not call the supplied function") {
    assert(buildList(0, (n: Int) => ???) == Nil)
  }

  test( "Does buildList work when n > 0?") {
    assert(buildList(3, (n: Int) => A(n)) == List(A(0), A(1), A(2)))
  }

  test("Does mapList work on empty lists?") {
    assert(mapList(Nil, (n: Int) => ???) == Nil)
  }

  test("Does mapList work on non-empty lists?") {
    assert(mapList[Int,Int](List(1,2,3), ((n: Int) => List(n, -n))) ==
      List(1, -1, 2, -2, 3, -3))
  }

  test("Does partition work on empty lists?") {
    assert(partition((x: Int) => true, Nil) == (Nil, Nil))
  }

  test("Does partition work on non-empty lists?") {
    assert(partition((x: Int) => x % 2 == 0, List(0, 1, 2, 3)) == 
      (List(0, 2), List(1, 3)))
  }

  test("Does merge work when lhs is Nil?") {
    val actual = merge(aLT, Nil, List(A(1), A(2), A(3)))
    val expected = List(A(1), A(2), A(3))
    assert(actual == expected || actual == expected.reverse)
  }

  test("Does merge work when rhs is Nil?") {
    val actual = merge(aLT, List(A(1), A(2), A(3)), Nil)
    val expected = List(A(1), A(2), A(3))
    assert(actual == expected || actual == expected.reverse)
  }

  test("Does merge interleave non-empty lists correctly?") {
    val expected = List(A(1), A(2), A(3), A(4), A(5), A(6))
    val r = try {
      val actual = merge(aLT, List(A(1), A(3), A(5)), List(A(2), A(4), A(6)))
      actual == expected || actual == expected.reverse
    }
    catch {
      case exn: Throwable => false
    }
    if (r == false) {
      val actual = merge(aLT, List(A(5), A(3), A(1)), List(A(6), A(4), A(2)))
      assert(actual == expected || actual == expected.reverse)
    }
  }

  test("Does sort work on Nil?") {
    assert(sort(aLT, Nil) == Nil)
  }

  test("Does sort work on an already sorted list?") {
    val actual = sort(aLT, List(A(1), A(2), A(3), A(4), A(5)))
    val expected = List(A(1), A(2), A(3), A(4), A(5))
    assert(actual == expected || actual == expected.reverse)
  }

  test("Does sort work on a reverse-sorted list?") {
    val actual = sort(aLT, List(A(5), A(4), A(3), A(2), A(1)))
    val expected = List(A(5), A(4), A(3), A(2), A(1))
    assert(actual == expected || actual == expected.reverse)
  }

  test("Does sort work on an unsorted list?") {
    val actual = sort(aLT, List(A(1), A(3), A(2), A(5), A(4)))
    val expected = List(A(5), A(4), A(3), A(2), A(1))
    assert(actual == expected || actual == expected.reverse)
  }

}
