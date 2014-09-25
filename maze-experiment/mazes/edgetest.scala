import edgemaze._
import scala.collection.immutable.Queue

val mazestr = "S#####.....##.#####.####...#.F##...#"
val g: MazeGraph = {
    val start = 0
    val finish = 10
    new MazeGraph(
        List(start,1,2,3,4,5,6,7,8,9,finish),
        List(
            // Start
            (start, 1, 1, South),
            // Node 1
            (1, start, 1, North),
            (1, 2, 1, East),
            // Node 2
            (2, 3, 3, East),
            (2, 4, 3, South),
            (2, 1, 1, West),
            // Node 3
            (3, 2, 3, West),
            // Node 4
            (4, 2, 3, North),
            (4, 6, 1, East),
            (4, 5, 1, West),
            // Node 5
            (5, 4, 1, East),
            // Node 6
            (6, 7, 1, South),
            (6, 4, 1, West),
            // Node 7
            (7, 6, 1, North),
            (7, 8, 2, East),
            // Node 8
            (8, 9, 1, North),
            (8, 7, 2, West),
            // Node 9
            (9, finish, 1, East),
            (9, 8, 1, South),
            // Finish
            (finish, 9, 1, West)),
        start, finish)
}
// Assumes that the path ends on a loop around the start node
def listwalk(m: Map[Graph.Node,Graph.Node], n: Graph.Node): List[Graph.Node] =
    if (n == m(n)) {
        List(n)
    } else {
        n :: listwalk(m, m(n))
    }

// the graph, the first node, the list of visited nodes, the stack to vist
def dfs(g: MazeGraph, v: Set[Graph.Node], s: List[Graph.Node]): Set[Graph.Node] = s match {
    case Nil => v
    case top :: stack =>
        if (!(v contains top)) {
            val vU = v + top
            dfs(g, vU, stack ++ g.adjacentTo( top))
        } else {
            dfs(g, v, stack)
        }
}

def depthFirstSearch(g: MazeGraph) = // this should be an object at this point
    dfs(g, Set[Graph.Node](), List(g.start))

// DFS with path to finish:
def dfsPath(g: MazeGraph, m: Map[Graph.Node,Graph.Node], s:List[(Graph.Node, Graph.Node)]): Map[Graph.Node,Graph.Node] =
    s match {
    case Nil => m
    case (top, pusher) :: stack =>
        if (!(m contains top)) {
            val mU = m + ((top, pusher))
            val adj = g.adjacentTo( top).map((a) => (a, top))
            dfsPath(g, mU, stack ++ adj)
        } else {
            dfsPath(g, m, stack)
        }
    }

def depthFirstSearchPath(g: MazeGraph): List[Graph.Node] =
    listwalk(
        dfsPath(g, Map[Graph.Node,Graph.Node](), List((g.start, g.start))),
        g.finish).reverse

// This is the exact same code as the dfs. Not worth doing, surely.
def bfs(g: MazeGraph, v: Set[Graph.Node], q: Queue[Graph.Node]): Set[Graph.Node] = {
    if (q.isEmpty) v
    else {
        val (top, newQ) = q.dequeue
        if (!(v contains top)) {
            val vU = v + top
            bfs(g, vU, newQ ++ g.adjacentTo(top))
        } else {
            bfs(g, v, newQ)
        }
    }
}

def dijkstraWalk(prev: Array[Graph.Node], curr: Graph.Node): List[Graph.Node] = {
    if (prev.isDefinedAt(curr))
        dijkstraWalk(prev, prev(curr)) ++ List(curr)
    else
        Nil
}

def dijkHelp(g: MazeGraph, dist: Array[Int], prev: Array[Graph.Node], toVisit: Set[Graph.Node]):
List[Graph.Node] = {
    if (toVisit.isEmpty)
        return prev.toList

    val curr = edgemaze.Util.minMember(dist, toVisit)
    if (curr == g.finish)
        return dijkstraWalk(prev, curr)

    val adj = g.adjacentTo( curr)
    adj.foreach( a => {
        val alt = dist(curr) + g.distanceBetween(curr, a).get
        if (alt < dist(a)) {
            dist(a) = alt
            prev(a) = curr
        }
    })
    dijkHelp(g, dist, prev, toVisit - curr)
}

// We could use Lists instead of Arrays by taking advantage of the 'updated'
// method. some_array(idx) = v OR some_list.updated(idx, v)
// The List approach would be fully recursive.
def dijkstra(g: MazeGraph): List[Graph.Node] = {
    val maxdist: Int = 10000
    val dist: Array[Int] =
        g.nodes.map((n) => if (n == g.start) 0 else maxdist).toArray
    val previous: Array[Graph.Node] = g.nodes.map(_ => -1).toArray
    val toVisit: Set[Graph.Node] = g.nodes.toSet

    dijkHelp(g, dist, previous, toVisit)
}
