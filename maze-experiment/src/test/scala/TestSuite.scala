class MyVertex extends Vertex[MyVertex, Int]

class TestSuite extends org.scalatest.FunSuite {

  test("mkEdge is symmetric") {
    val vx1 = new MyVertex()
    val vx2 = new MyVertex()
    assert(vx1.mkEdge(100, vx2))
    assert(vx1.isNeighbor(vx2))
    assert(vx2.isNeighbor(vx1))
  }

  test("rmEdge works") {
    val vx1 = new MyVertex()
    val vx2 = new MyVertex()
    assert(vx1.mkEdge(100, vx2))
    assert(vx2.rmEdge(vx1))
    assert(!vx1.isNeighbor(vx2))
    assert(!vx2.isNeighbor(vx1))
  }

  test("no self-loops") {
    val vx1 = new MyVertex()
    assert(!vx1.mkEdge(200, vx1))
  }

  test("not a multigraph") {
    val vx1 = new MyVertex()
    val vx2 = new MyVertex()
    assert(vx1.mkEdge(100, vx2))
    assert(!vx1.mkEdge(200, vx2))
  }

}