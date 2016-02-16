package SimpleGraph {

  class Vertex[V](val value: V, val neighbors: collection.mutable.Set[Vertex[V]])

  class Graph[V]() {

    val vertices = collection.mutable.Set[Vertex[V]]()

    def newVertex(x: V) = {
      val vx = new Vertex(x, collection.mutable.Set[Vertex[V]]())
      vertices += vx
      vx
    }

    def newEdge(v1: Vertex[V], v2: Vertex[V]) = {
      v1.neighbors += v2
    }
  }

}

package DepGraph {

  class Graph[V]() {

    class Vertex(val value: V, val neighbors: collection.mutable.Set[Vertex])

    val vertices = collection.mutable.Set[Vertex]()

    def newVertex(x: V) = {
      val vx = new Vertex(x, collection.mutable.Set[Vertex]())
      vertices += vx
      vx
    }

    def newEdge(v1: Vertex, v2: Vertex) = {
      v1.neighbors += v2
    }
  }

  object Use {

    val g1 = new Graph[Int]()

    val g2 = new Graph[Int]()

    val v1: g1.Vertex = g1.newVertex(10)

    val v2: g1.Vertex = g2.newVertex(20)

   g1.newEdge(v1, v2)

  }


}