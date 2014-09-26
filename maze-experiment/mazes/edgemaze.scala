// package edgemaze
// 6 x 6:
// S##### S##### 11 nodes
// .....# 12..3#
// #.#### #.####
// #.#### #.####
// ...#.F 546#9F
// ##...# ##7.8#
import scala.collection.immutable.Queue

sealed trait Direction
object North extends Direction
object South extends Direction
object East extends Direction
object West extends Direction

type Node = Int
type Edge = (Node, Node, Int, Direction)
type Graph = (List[Node], List[Edge])

val mazestr = "S#####.....##.#####.####...#.F##...#"

val start = 0
val finish = 10
val g: Graph = (
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
         (finish, 9, 1, West)))

def nodes(g: Graph): List[Node] = g._1
def edges(g: Graph): List[Edge] = g._2
def adjacentTo(g: Graph, n: Node): List[Node] =
    edges(g).collect{ case (v, w, _, _) if (n == v) => w }
def distanceBetween(g: Graph, v: Node, w: Node): Option[Int] =
    edges(g).find((e: Edge) => (e._1 == v && e._2 == w)) match {
        case None => None
        case Some((_, _, distance, _)) => Some(distance)
    }

// the graph, the first node, the list of visited nodes, the stack to vist
def dfs(g: Graph, v: Set[Node], s: List[Node]): Set[Node] = s match {
    case Nil => v
    case top :: stack =>
        if (!(v contains top)) {
            val vU = v + top
            dfs(g, vU, stack ++ adjacentTo(g, top))
        } else {
            dfs(g, v, stack)
        }
}

def depthFirstSearch(g: Graph) = // this should be an object at this point
    dfs(g, Set[Node](), List(start))

// DFS with path to finish:
def dfsPath(g: Graph, m: Map[Node,Node],
    s:List[(Node, Node)]): Map[Node,Node] = s match {
    case Nil => m
    case (top, pusher) :: stack =>
        if (!(m contains top)) {
            val mU = m + ((top, pusher))
            val adj = adjacentTo(g, top).map((a) => (a, top))
            dfsPath(g, mU, stack ++ adj)
        } else {
            dfsPath(g, m, stack)
        }
}

def listwalk(m: Map[Node,Node], n: Node): List[Node] =
    if (n == start) {
        List(n)
    } else {
        n :: listwalk(m, m(n))
    }

def depthFirstSearchPath(g: Graph): List[Node] =
    listwalk(
        dfsPath(g, Map[Node,Node](), List((start, start))),
        finish).reverse

def printwalk(m: Map[Node,Node], n: Node): Unit =
    if (n == start) {
        println(s"Start: $n")
    } else if (n == finish) {
        println(s"Finish: $n")
        printwalk(m, m(n))
    } else {
        println(n)
        printwalk(m, m(n))
    }

def pathLength(g: Graph, l: List[Node]): Int = l match {
    case Nil => 0
    case head :: Nil => 0
    case h1 :: h2 :: rest => distanceBetween(g, h1, h2) match {
        case None => sys.error(s"No edge between $h1 and $h2. Path incorrect.")
        case Some(dist) => dist + pathLength(g, h2 :: rest)
    }
}

// This is the exact same code as the dfs. Not worth doing, surely.
def bfs(g: Graph, v: Set[Node], q: Queue[Node]): Set[Node] = {
    if (q.isEmpty) v
    else {
        val (top, newQ) = q.dequeue
        if (!(v contains top)) {
            val vU = v + top
            bfs(g, vU, newQ ++ adjacentTo(g, top))
        } else {
            bfs(g, v, newQ)
        }
    }
}

def indexOfMin[A : Ordering](arr: Array[A]): Int =
    arr.zipWithIndex.minBy(a => a._1)._2

def nodeWithMinDist(dist: Array[Int], nodes: Set[Node]): Node = {
    val min = indexOfMin(dist)
    if (nodes contains min)
        min
    else
        nodeWithMinDist(dist.updated(min, 10000), nodes)
}

def dijkstraWalk(prev: Array[Node], curr: Node): List[Node] = {
    if (prev.isDefinedAt(curr))
        dijkstraWalk(prev, prev(curr)) ++ List(curr)
    else
        Nil
}

def dijkHelp(g: Graph, dist: Array[Int], prev: Array[Node], toVisit: Set[Node]):
List[Node] = {
    if (toVisit.isEmpty)
        return prev.toList

    val curr = nodeWithMinDist(dist, toVisit)
    if (curr == finish)
        return dijkstraWalk(prev, curr)

    val adj = adjacentTo(g, curr)
    adj.foreach( a => {
        val alt = dist(curr) + distanceBetween(g, curr, a).get
        if (alt < dist(a)) {
            dist(a) = alt
            prev(a) = curr
        }
    })
    dijkHelp(g, dist, prev, toVisit - curr)
}

def dijkstra(g: Graph): List[Node] = {
    val maxdist: Int = 10000
    val dist: Array[Int] =
        nodes(g).map((n) => if (n == start) 0 else maxdist).toArray
    val previous: Array[Node] = nodes(g).map(_ => -1).toArray
    val toVisit: Set[Node] = nodes(g).toSet

    dijkHelp(g, dist, previous, toVisit)
}
