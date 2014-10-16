import cmpsci220.hw.graph._
import cmpsci220.hw.disjointset._

object MST {

  def compareEdge[V](edge1: (V, Int, V), edge2: (V, Int, V)): Boolean = {
    edge1._2 < edge2._2
  }

  def allEdges[V](graph: Graph[V, Int]): List[(V, Int, V)] = {
    var allEdges = List[(V, Int, V)]()
    for (nodes <- graph.nodes.tails.filter(_.length >= 2)) {
      val vx1: V = nodes.head
      for (vx2 <- nodes.tail) {
        if (graph.neighbors(vx1).contains(vx2)) {
          allEdges = (vx1, graph.getEdge(vx1, vx2), vx2) :: allEdges
        }
      }
    }
    allEdges.sortWith(compareEdge)
  }


  def mst[V](graph: Graph[V,Int]): Graph[V,Int] = {
    val r = new Graph[V,Int]
    val sets = new DisjointSet[V]()
    for (node <- graph.nodes) {
      r.mkNode(node)
    }
    for ((vx1, weight, vx2) <- allEdges(graph)) {
      if (!sets.inSameSet(vx1, vx2)) {
        r.mkEdge(vx1, weight, vx2)
        sets.union(vx1, vx2)
      }
      else {
        println(s"$vx1 and $vx2 are in the same set")
      }
    }
    r
  }


}