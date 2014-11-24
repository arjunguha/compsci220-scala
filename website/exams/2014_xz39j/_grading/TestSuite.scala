package cmpsci220.grading

import grader.TestSuite

object Main extends App {

  import Solution._

  TestSuite("grading.yaml") { builder =>

    import builder._

    test("derivatives", points = 25) {
      def f(x: Double): Double = x * x * x
      def df(x: Double): Double = 3 * x * x
      assert(math.abs(ddx(f)(100) - df(100)) <= 1)
      assert(math.abs(ddx(f)(1000) - df(100)) <= 1)
    }

    test("isMirrored test 1", points = 15) {
      assert(isMirrored(Node(Node(Leaf(1), Node(Leaf(2), Leaf(3))),
                             Node(Node(Leaf(3), Leaf(2)), Leaf(1)))))
    }

    test("isMirrored test 2", points = 5) {
      assert(!isMirrored(Node(Node(Leaf(1), Node(Leaf(2), Leaf(3))),
                              Node(Node(Leaf(2), Leaf(3)), Leaf(1)))))
    }

    test("isMirrored test 3", points = 5) {
      assert(!isMirrored(Node(Node(Leaf(1), Node(Leaf(2), Leaf(3))),
                              Node(Leaf(2), Leaf(1)))))
    }

    test("filterIndex test 1", points = 5) {
      assert(filterIndex(n => false, List(1,2,3)) == List())
    }

    test("filterIndex test 2", points = 5) {
      assert(filterIndex(n => true, List(1,2,3)) == List(1, 2, 3))
    }

    test("filterIndex test 3", points = 5) {
      assert(filterIndex(n => n % 2 == 0, List("a", "b", "c", "d")), List("a", "c"))
    }

    test("filterIndex test 4", points = 5) {
      assert(filterIndex(n => n == 2, List("a", "b", "c", "d")), List("c"))
    }

    test("filterIndex test 5", points = 5) {
      assert(filterIndex(n => n > 2, List("a", "b", "c", "d")), List("d"))
    }

    test("same fringe test 1", points = 10) {
      val example1 = Node(Node(Leaf(10), Leaf(20)), Node(Leaf(30), Leaf(40)))
      val example2 = Node(Leaf(10), Node(Leaf(20), Node(Leaf(30), Leaf(40))))
      assert(sameFringe(fringe(example1), fringe(example2)))
    }

    test("same fringe test 2", points = 15) {
      val example3 = fringe(Node(Leaf(1), Node(Leaf(4), Leaf(6))))
      val example4 = fringe(Node(Leaf(7), Node(Leaf(7), Leaf(6))))
      assert(!sameFringe(example3, example4))
      assert(!example3.hasDefiniteSize)
      assert(!example4.hasDefiniteSize)
    }

    test("regex test 1", points = 5) {
      assert(notAbb.findFirstIn("abb").isEmpty)
    }

    test("regex test 2", points = 5) {
      assert(notAbb.findFirstIn("ababababab").isEmpty == false)
    }

    test("regex test 3", points = 5) {
      assert(notAbb.findFirstIn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbaaaaaaaaaaaa").isEmpty)
    }

    test("regex test 4", points = 5) {
      assert(notAbb.findFirstIn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabaaaaaaaaaaaaaaa").isEmpty == false)
    }


    test("regex test 5", points = 5) {
      assert(notAbb.findFirstIn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabaaaaaaaaaaaaaaaabbaaa").isEmpty)
    }

  }

}