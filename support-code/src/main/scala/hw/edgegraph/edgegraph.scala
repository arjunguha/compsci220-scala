package edgemaze

package object Graph {
    type Node = Int
    type Edge = (Node, Node, Int)
}

class EdgeGraph(n: List[Graph.Node], e: List[Graph.Edge]) {
    private val nodes = n
    private val edges = e

    def getNodes(): List[Graph.Node] = nodes
    def getEdges(): List[Graph.Edge] = edges

    def adjacentTo(n: Graph.Node): List[Graph.Node] =
        edges.collect{ case (v, w, _) if (n == v) => w }

    def edgeBetween(v: Graph.Node, w: Graph.Node): Option[Graph.Edge] =
        edges.find(e => (e._1 == v && e._2 == w))

    def distanceBetween(v: Graph.Node, w: Graph.Node): Option[Int] =
        edgeBetween(v, w) match {
            case None => None
            case Some((_, _, distance)) => Some(distance)
        }
}
object EdgeGraph {
    def apply(n: List[Graph.Node], e: List[Graph.Edge]) = new EdgeGraph(n,e)
    def apply(e: List[Graph.Edge]) =
        new EdgeGraph(
            e.foldLeft(List[Graph.Node]())((l, p) =>
                    p._1 :: p._2 :: l).distinct,
                e)
}

package object util {
    // Find the member of the set that has the corresponding value in the given
    // array
    def minMember(values: Array[Int], set: Set[Int]): Int = {
        set.map(i => (i, values(i))).minBy(t => t._2)._1
    }
}

package object TestGraphs {
    val small: EdgeGraph =
        EdgeGraph(
            List(
                (0, 1, 12),
                (0, 2, 2),
                (1, 0, 12),
                (1, 3, 3),
                (2, 0, 2),
                (2, 4, 3),
                (3, 1, 3),
                (3, 5, 1),
                (4, 2, 3),
                (4, 5, 2),
                (5, 3, 1),
                (5, 4, 2)))

    val medium: EdgeGraph =
        EdgeGraph(
            List(0,1,2,3,4,5,6,7,8,9,10),
            List(
                // Node 0
                (0, 1, 1),
                // Node 1
                (1, 0, 1),
                (1, 2, 1),
                // Node 2
                (2, 3, 3),
                (2, 4, 3),
                (2, 1, 1),
                // Node 3
                (3, 2, 3),
                // Node 4
                (4, 2, 3),
                (4, 6, 1),
                (4, 5, 1),
                // Node 5
                (5, 4, 1),
                // Node 6
                (6, 7, 1),
                (6, 4, 1),
                // Node 7
                (7, 6, 1),
                (7, 8, 2),
                // Node 8
                (8, 9, 1),
                (8, 7, 2),
                // Node 9
                (9, 10, 1),
                (9, 8, 1),
                // Node 10
                (10, 9, 1)))
}
