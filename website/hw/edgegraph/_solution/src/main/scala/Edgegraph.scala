import cmpsci220.hw.edgemaze._

object Solution extends EdgeGraphFunctions {
    def reachable(g: EdgeGraph, start: Graph.Node): Set[Graph.Node] = {
        val visited = Set[Graph.Node]()
        val stack = List(start)

        def _reach(g: EdgeGraph, v: Set[Graph.Node],
                   s: List[Graph.Node]): Set[Graph.Node] = s match {
            case Nil => p
            case top :: rest =>
                if (!(v contains top)) {
                    val adj = g.adjacentTo(top)
                    _dfs(g, v + top, rest ++ adj)
                } else {
                    _dfs(g, v, rest)
                }
            }
        _reach(g, visited, stack)
    }

    def dfsPath(g: EdgeGraph, start: Graph.Node): Array[Graph.Node] = {
        val visited = Set[Graph.Node]()
        val stack = List(start)
        val prev = g.nodes.map(e => -1).toArray

        def _dfs(g: EdgeGraph, v: Set[Graph.Node], s: List[Graph.Node],
                 p: Array[Graph.Node]): Array[Graph.Node] = s match {
            case Nil => p
            case top :: rest =>
                if (!(v contains top)) {
                    val adj = g.adjacentTo(top)
                    adj.filter(a => p(a) == -1).foreach ( a => p(a) = top )
                    _dfs(g, v + top, rest ++ adj, p)
                } else {
                    _dfs(g, v, rest, p)
                }
        }
        _dfs(g, visited, stack, prev)
    }

    def dijkstra(g: EdgeGraph, start: Graph.Node): Array[Graph.Node] = {
        val maxdist: Int = 10000
        val dist: Array[Int] =
            g.nodes.map((n) => if (n == start) 0 else maxdist).toArray
        val previous: Array[Graph.Node] = g.nodes.map(_ => -1).toArray
        val toVisit: Set[Graph.Node] = g.nodes.toSet

        def _dijk(g: EdgeGraph, v: Set[Graph.Node], d: Array[Int],
                  p: Array[Graph.Node]): Array[Graph.Node] = {
            if (v.isEmpty)
                return p

            val curr = util.minMember(d, v)
            val adj = g.adjacentTo(curr)
            adj.filter(v contains _).foreach ( a => {
                val td = dist(curr) + g.distanceBetween(curr, a).get
                if (td < dist(a)) {
                    d(a) = td
                    p(a) = curr
                }
            })
            _dijk(g, v - curr, d, p)
        }
        _dijk(g, toVisit, dist, previous)
    }

    // Create a path from adjacency list
    def makePath(adj: Array[Graph.Node], end: Graph.Node): List[Graph.Node] = {
        def _mp(curr: Graph.Node, l: List[Graph.Node]): List[Graph.Node] = {
            if (adj(curr) == -1) {
                curr :: l
            } else {
                _mp(adj(curr), curr :: l)
            }
        }
        _mp(end, Nil)
    }

    // When grading this, make sure they check EVERY consecutive pair of nodes
    // calling ... && isValidPath(g, rest) will only check every other pair.
    // e.g. List(1,2,3,4) should be processed (1,2), (2,3), (3,4), not (1,2), (3,4)
    def isValidPath(g: EdgeGraph, path: List[Graph.Node]): Boolean = path match {
        case n1 :: n2 :: rest =>
            g.edgeBetween(n1, n2).isDefined && isValidPath(g, n2 :: rest)
        case n :: Nil => true
    }

    // Assumes a valid path
    def pathLength(g: EdgeGraph, path: List[Graph.Node]): Int = path match {
        case n1 :: n2 :: rest =>
            g.distanceBetween(n1, n2).get + pathLength(g, n2 :: rest)
        case _ => 0
    }
}
