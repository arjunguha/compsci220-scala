package edgemaze
// 6 x 6:
// S##### S##### 11 nodes
// .....# 12..3#
// #.#### #.####
// #.#### #.####
// ...#.F 546#9F
// ##...# ##7.8#

class MazeGraph(n: List[Graph.Node], e: List[Graph.Edge], s: Graph.Node, f:
    Graph.Node) {
    val nodes = n
    val edges = e

    val start = s
    val finish = f

    def adjacentTo(n: Graph.Node): List[Graph.Node] =
        edges.collect{ case (v, w, _, _) if (n == v) => w }

    def distanceBetween(v: Graph.Node, w: Graph.Node): Option[Int] =
        edges.find((e: Graph.Edge) => (e._1 == v && e._2 == w)) match {
            case None => None
            case Some((_, _, distance, _)) => Some(distance)
        }

    def pathLength(l: List[Graph.Node]): Int = l match {
        case Nil => 0
        case head :: Nil => 0
        case h1 :: h2 :: rest => this.distanceBetween(h1, h2) match {
            case None => sys.error(s"No edge between $h1 and $h2. Path incorrect.")
            case Some(dist) => dist + pathLength(h2 :: rest)
        }
    }
}

object MazeGraph {
    def apply(n: List[Graph.Node], e: List[Graph.Edge], s: Graph.Node, f:
        Graph.Node): MazeGraph = new MazeGraph(n,e,s,f)
}

object Util {
    def minMember(values: Array[Int], set: Set[Int]): Int = {
        set.map(i => (i, values(i))).minBy(t => t._2)._1
    }
}
