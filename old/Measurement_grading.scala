package cmpsci220.grading

import cmpsci220._
import cmpsci220.hw.measurement._
import grader.TestSuite

class Measurement(targetYaml: String, solution: MeasurementFunctions) {

  import solution._

  TestSuite(targetYaml) { builder =>
    import builder._

    test("Does time work? (Checking lower-bound)") {
      def f(n: Int) = {
        Thread.sleep(n)
      }
      assert(solution.time(f, 1000) > 500)
    }

    test("Does average time call the function exactly N times?") {
      var i = 0
      def f(n: Int) {
        i = i + 1
      }

      averageTime(7, f, 0)
      assert(i == 7)
    }

    test("Does revOrder(5) work?") {
      assert(revOrder(5) == List(5, 4, 3, 2, 1))
    }

    test("Does revOrder(0) work?") {
      assert(revOrder(0) == Empty())
    }

    test("Does randomInts(n) produce n integers?") {
      assert(length(randomInts(10)) == 10)
    }

    test("Do two successive calls to randomInts(n) return different values?") {
      val lst1 = randomInts(2)
      val lst2 = randomInts(2)
      assert(lst1 != lst2)
    }

    val lst = List(2, 3, 5, 7, 11, 13)

    test("isMemberAllOrdList(lst, insertAllOrdList(lst))?") {
      assert(isMemberAllOrdList(lst, insertAllOrdList(lst)))
    }

    test("isMemberAllBST(lst, insertAllBST(lst))?") {
      assert(isMemberAllBST(lst, insertAllBST(lst)))
    }

    test("isMemberAllAVL(lst, insertAllAVL(lst))?") {
      assert(isMemberAllAVL(lst, insertAllAVL(lst)))
    }

    test("isMemberAllOrdList base case") {
      assert(isMemberAllOrdList(Empty(), emptyOrdList))
    }

    test("isMemberAllBST base case") {
      assert(isMemberAllBST(Empty(), emptyBST))
    }

    test("isMemberAllAVL base case") {
      assert(isMemberAllAVL(Empty(), emptyAVL))
    }

    test("isMemberAllBST should produce false on an emptyBST") {
      assert(isMemberAllBST(List(1, 2, 3), emptyBST) == false)
    }

    test("Does insertAll work with a new data structure?", 15) {
      def insert(x: Int, set: Set[Int]): Set[Int] = set + x
      val empty = Set.empty[Int]
      assert(insertAll(empty, insert, List(1, 3, 5, 7)) == Set(1, 3, 5, 7))
    }

    test("Does isMemberAll work with a new data structure?", 15) {
      def isMember(x: Int, s: Set[Int]) = s.contains(x)
      val set = Set(1, 3, 5, 7)
      assert(isMemberAll(isMember, List(1, 3, 5, 7), set))
    }

    test("On a new data strucuture, does isMemberAll fail if an element is missing?", 15) {
      def isMember(x: Int, s: Set[Int]) = s.contains(x)
      val set = Set(1, 3, 5, 7)
      assert(isMemberAll(isMember, List(1, 3, 2, 5, 7), set) == false)
    }


  }
}