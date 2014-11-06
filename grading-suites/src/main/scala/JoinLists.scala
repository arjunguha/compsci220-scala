package cmpsci220.grading

import cmpsci220.hw.joinlists._
import grader.TestSuite

class JoinLists(targetYaml: String, solution: JoinListFunctions) {

  import solution._

  TestSuite(targetYaml) { builder =>

    import builder._

    def gt(x: Int, y: Int) = x > y

    test("Does max(Empty()) produce None?") {
      assert(max(Empty[Int](), gt) == None)
    }

    test("Does max on a singleton produce the value?") {
      assert(max(Singleton(100), gt) == Some(100))
    }

    test("Does max on a JoinList that only has 1 Singleton produce the value?") {
      assert(max(Join(Singleton(100), Empty(), 1), gt) == Some(100))
    }

    test("Does max produce the maximum on a larger JoinList?") {
      assert(max(fromList(List(1, 7, 2, 3, 9, 200, 1)), gt) == Some(200))
    }

    test("Does max work with a non-integer compare function?") {
      def f(x: String, y: String): Boolean = x == "MAX"
      assert(max(fromList(List("a", "b", "MAX", "c", "d", "e")), f) == Some("MAX"))
    }

    def add1(x: Int) = x + 1

    test("Does map work on empty lists?") {
      assert(map(add1, Empty()) == Empty())
    }

    test("Does map produce singletons on singleton lists?") {
      assert(map(add1, Singleton(10)) == Singleton(11))
    }

    test("Does map work on larger lists?") {
      assert(toList(map(add1, fromList(List(1, 2, 3, 4, 5, 6)))) ==
             List(2, 3, 4, 5, 6, 7))
    }

    def isEven(x: Int) = x % 2 == 0

    test("Does filter work on empty lists?") {
      assert(filter(isEven, Empty[Int]()) == Empty())
    }

    test("Does filter produce empty when filtering out a singleton?") {
      assert(filter(isEven, Singleton(1)) == Empty())
    }

    test("Does filter work on larger lists?") {
      assert(toList(filter(isEven, fromList(List(1,2, 3, 4, 5, 6, 7, 8)))) ==
             List(2, 4, 6, 8))
    }

    test("Does first(Empty) produce None?") {
      assert(first(Empty[Int]()) == None)
    }

    test("Does first(Singleton(x)) produce Some(x)?") {
      assert(first(Singleton(10)) == Some(10))
    }

    test("Does first work on larger lists?") {
      assert(first(fromList(List(10, 20, 30, 40, 50, 60, 70))) == Some(10))
    }

    test("Does rest(Empty) produce None?") {
      assert(rest(Empty[Int]()) == None)
    }

    test("Does rest(Singleton(x)) produce Some(Empty())?") {
      assert(rest(Singleton(10)) == Some(Empty[Int]()))
    }

    val jl1 = fromList(List(10, 20, 30, 40, 50, 60, 70))

    test("Does rest work on larger lists?") {
      rest(jl1) match {
        case Some(tail) => assert(toList(tail) == List(20, 30, 40, 50, 60, 70))
        case None  => assert(false)
      }
    }

    test("Does nth(Empty, _) produce None?") {
      assert(nth(Empty[Int], 0) == None)
    }

    test("Does nth(Singleton(x), 0) produce Some(x)?") {
      assert(nth(Singleton(42), 0) == Some(42))
    }

    test("Does nth(Singleton(x), 100) produce None?") {
      assert(nth(Singleton(1), 100) == None)
    }

    test("Does nth work with a valid index on a larger lists?") {
      assert(nth(jl1, 1) == Some(20))
    }

    test("Does nth produce None when indexing out-of-bounds on a larger list?") {
       assert(nth(jl1, 50) == None)
    }

  }

}
