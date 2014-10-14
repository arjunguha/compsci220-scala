import org.scalatest._
import cmpsci220.hw.graph._

class GraphSuite extends FunSuite {

	test("creating nodes") {
		val g = new Graph[String, Int]()
		assert(g.mkNode("Seattle"))
		assert(g.mkNode("Portland"))
		assert(!g.mkNode("Seattle"))
	}

	test("creating edges") {
    val g = new Graph[String, Int]()
    assert(g.mkNode("Seattle"))
    assert(g.mkNode("Portland"))
		assert(g.mkEdge("Seattle", 200, "Portland"))
    assert(!g.mkEdge("Seattle", 200, "Portland"))
    assert(!g.mkEdge("Seattle", 200, "Toronto")) // toronto is not an edge
	}	

  test("querying neighbors and edges") {
    val g = new Graph[String, Int]()
    assert(g.mkNode("Seattle"))
    assert(g.mkNode("Portland"))
    assert(g.mkNode("Vancouver"))
    assert(g.mkEdge("Seattle", 200, "Portland"))
    assert(g.mkEdge("Seattle", 300, "Vancouver"))

    assert(g.neighbors("Seattle") == Set("Portland", "Vancouver"))
    assert(g.neighbors("Portland") == Set("Seattle"))
    assert(g.neighbors("Vancouver") == Set("Seattle"))
    assert(g.neighbors("San Francisco").isEmpty)
    assert(g.getEdge("Vancouver", "Seattle") == 300)
  }

  test("assignment text") {
    val g = new Graph[String, Double]()
    assert(g.mkNode("Amherst"))
    assert(g.mkNode("Northampton"))
    assert(g.mkEdge("Amherst", 7.9, "Northampton"))
    assert(!g.mkEdge("Northampton", 19.5, "Springfield"))
    assert(g.mkNode("Springfield"))
    assert(g.mkEdge("Northampton", 19.5, "Springfield"))
    assert(!g.mkEdge("Northampton", 0, "Northampton"))
    assert(g.getEdge("Amherst", "Northampton") == 7.9)
    assert(g.getEdge("Northampton", "Amherst") == 7.9)
    assert(g.neighbors("Northampton") == Set("Amherst", "Springfield"))

  }

  test("edge-list constructor") {
    val g = Graph(("Amherst", 7.9, "Northampton"),
                  ("Amherst", 19.5, "Springfield"))
  }



}
