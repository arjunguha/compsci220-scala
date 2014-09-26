package edgemaze
// 6 x 6:
// S##### S##### 11 nodes
// .....# 12..3#
// #.#### #.####
// #.#### #.####
// ...#.F 546#9F
// ##...# ##7.8#

sealed trait Direction
case class North() extends Direction
case class South() extends Direction
case class East() extends Direction
case class West() extends Direction

package object Graph {
    type Node = Int
    type Edge = (Node, Node, Int, Direction)
}

class MazeGraph(n: List[Graph.Node], e: List[Graph.Edge], s: Graph.Node, f:
    Graph.Node) {
    val nodes = n
    val edges = e

    val start = s
    val finish = f

    def adjacentTo(n: Graph.Node): List[Graph.Node] =
        edges.collect{ case (v, w, _, _) if (n == v) => w }

    def edgeBetween(v: Graph.Node, w: Graph.Node): Option[Graph.Edge] =
        edges.find(e => (e._1 == v && e._2 == w))

    def distanceBetween(v: Graph.Node, w: Graph.Node): Option[Int] =
        edgeBetween(v, w) match {
            case None => None
            case Some((_, _, distance, _)) => Some(distance)
        }

    def directionBetween(v: Graph.Node, w: Graph.Node): Option[Direction] =
        edgeBetween(v, w) match {
            case None => None
            case Some((_, _, _, dir)) => Some(dir)
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

package object util {
    // Find the member of the set that has the corresponding value in the given
    // array
    def minMember(values: Array[Int], set: Set[Int]): Int = {
        set.map(i => (i, values(i))).minBy(t => t._2)._1
    }
}


