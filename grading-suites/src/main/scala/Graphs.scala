package cmpsci220.grading

import cmpsci220.hw.graph._
import grader.TestSuite

class Graphs(targetYaml: String, solution: GraphAlgorithms) {

  import solution._

  def mkTwoGraphs[V,E](edges: (V, E, V)*): (Graph[V,E], Graph[V,E]) = {
    (Graph(edges :_*), Graph(edges :_*))
  }


  TestSuite(targetYaml) { builder =>
    import builder._

    test("Does reachable work on a fully disconnected graph?") {
      val g = new Graph[Int, Int]()
      g.mkNode(1)
      g.mkNode(2)
      g.mkNode(3)
      assert(solution.reachable(g, 2) == Set(2))
    }

    def disconnected(): Graph[String, Unit] = {
       Graph(("A", (), "B"), ("B", (), "C"), ("C", (), "D"), ("D", (), "A"))
     }

    test("Does reachable work on graph with a 4-cycle?") {
      val g = disconnected()
      assert(solution.reachable(g, "C") == Set("A", "B", "C", "D"))
    }

    test("Can isValidPath handle the empty graph with bogus nodes?") {
      val g = new Graph[Int, Int]()
      assert(solution.isValidPath(g, List(1, 2, 3)) == false)
    }

    // TODO(arjun): must test for negative path too
    test("Does isValidPath work with a legitimate path?") {
       val g = Graph(("A", (), "B"), ("B", (), "C"), ("C", (), "D"),
                     ("C", (), "E"), ("D", (), "A"))
       assert(solution.isValidPath(g, List("A", "B", "C", "E")))
    }

    test("Does BFS work correctly on a disconnected graph?") {
     val g = disconnected()
     val s = solution.breathFirstSearch(g, "A", "B")
     assert(!g.getVisitOrder.contains("B"))
    }

    test("Does DFS work correctly on a disconnected graph?") {
     val g = disconnected()
     val s = solution.depthFirstSearch(g, "A", "B")
     assert(!g.getVisitOrder.contains("B"))
    }

    test("Does BFS traverse breadth-first?") {
      val g = Graph(("Root", (), "A"), ("Root", (), "B"),
                    ("A", (), "A1"), ("B", (), "B1"),
                    ("B1", (), "B2"))
      val r = solution.breathFirstSearch(g, "Root", "B2")
      val order = g.getVisitOrder
      val level1 = order.takeWhile(_ != "A") ++ order.takeWhile(_ != "B")
      assert((level1.toSet intersect Set("A1", "B1", "B2")) == Set())
      val level2 = order.takeWhile(_ != "A1") ++ order.takeWhile(_ != "B1")
      assert((level2.toSet intersect Set("B2")) == Set())
    }

    test("Does DFS traverse depth-first?") {
      val g = Graph(("Root", (), "A"), ("Root", (), "B"),
                    ("A", (), "A1"), ("B", (), "B1"),
                    ("B1", (), "B2"), ("B2", (), "B3"))
      val r = solution.depthFirstSearch(g, "Root", "B3")
      val order = g.getVisitOrder
      val path = order.dropWhile(_ != "B").take(3)
      assert(path == List("B", "B1", "B2"))
    }

    test("Does shortestPath work?") {
      val g = Graph[String, Float](("A", 200, "B"), ("A", 10, "C"), ("C", 10, "D"), ("D", 10, "E"),
                     ("B", 10, "E"))
      assert (solution.shortestPath(g, "A", "E") == 30)
    }

  }

}
