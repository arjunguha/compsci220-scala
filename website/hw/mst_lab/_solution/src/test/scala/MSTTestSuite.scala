import cmpsci220.hw.graph._
import MST._

class MSTTestSuite extends org.scalatest.FunSuite {


  test("simple MST") {
    val g = Graph(("A", 10, "B"),
                  ("B", 20, "C"),
                  ("A", 50, "C"),
                  ("A", 6, "D"),
                  ("C", 7, "D"))
    val t = mst(g)

    for (ns <- t.nodes.tails.filter(_.length >= 2)) {
      val n1 = ns.head
      for (n2 <- ns.tail) {
        if (t.neighbors(n1).contains(n2)) {
          println(s"$n1 -- ${t.getEdge(n1, n2)} -- $n2")
        }
      }
    }
  }

}