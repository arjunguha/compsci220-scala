package edgemaze
// 6 x 6:
// S##### S##### 11 nodes
// .....# 12..3#
// #.#### #.####
// #.#### #.####
// ...#.F 546#9F
// ##...# ##7.8#

package object Graph {
    type Node = Int
    type Edge = (Node, Node, Int)
}

class EdgeGraph(n: List[Graph.Node], e: List[Graph.Edge]) {
    val nodes = n
    val edges = e

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


