import cmpsci220.hw.graph._
import MST._

class MSTTestSuite extends org.scalatest.FunSuite {

  test("MST test1") {
    val g = Graph(("A", 10, "B"),
      ("B", 20, "C"),
      ("A", 50, "C"))
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

  test("MST test2") {
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

  test("MST test3") {
    val g = Graph(("A", 4, "B"),
      ("B", 3, "C"),
      ("C", 2, "D"),
      ("D", 1, "E"),
      ("E", 1, "F"),
      ("F", 5, "A"),
      ("A", 3, "D"),
      ("B", 4, "D"))
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

  test("MST test4") {
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

  test("MST test5") {
    val g = Graph(("A", 6, "B"),
      ("B", 5, "C"),
      ("C", 1, "A"),
      ("C", 5, "D"),
      ("C", 6, "E"),
      ("C", 4, "F"),
      ("D", 2, "F"),
      ("E", 6, "F"),
      ("A", 5, "D"),
      ("B", 3, "E"))
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