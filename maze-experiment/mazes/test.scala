import maze._

def zipWith[A,B](lst: List[A], elem: B): List[(A,B)] = lst match {
    case Nil => Nil
    case head :: tail => (head, elem) :: zipWith(tail, elem)
}

val mazestr = "S#####.....##.#####.####...#.F##...#"

val m = Maze(6,6, mazestr)

object DepthFirstSearch {
    def dfs(m: Maze): List[MazeNode] =
        dfsHelp(m, Set[MazeNode](), List(m.start)).filter{
            case Wall() => false
            case _ => true }

    def dfsHelp(m: Maze, d: Set[MazeNode], s: List[MazeNode]): List[MazeNode]
    = s match {
        case Nil => d.toList
        case top :: stack =>
            if (!(d contains top)) {
                val dU = d + top
                dfsHelp(m, dU, stack ++ m.adjacentVertices(top))
            } else {
                dfsHelp(m, d, stack)
            }
    }

    def dfsPath(m: Maze): Map[MazeNode,MazeNode] =
        dfsPh(m, Map(), m.start, List((m.start, m.start)))

    def dfsPh(m: Maze, map: Map[MazeNode,MazeNode], prev: MazeNode, s:
        List[(MazeNode, MazeNode)]): Map[MazeNode,MazeNode] = s match {
        case Nil => map
        case (Wall(), _) :: stack => dfsPh(m, map, prev, stack)
        case (top, pusher) :: stack =>
            if ((map get top) == None) {
                val mU = map + ((top, pusher))
                //
                println(s"Pushing $top, $prev")
                val adj = zipWith(m.adjacentVertices(top), top)
                println(s"Adjacent to $top: $adj")
                //
                dfsPh(m, mU, top, stack ++ zipWith(m.adjacentVertices(top),
                    top))
            } else {
                dfsPh(m, map, prev, stack)
            }
    }
}

def walk(map: Map[MazeNode,MazeNode], curr: MazeNode): Unit = curr match {
    case Start(v) => print(curr); return
    case _ => println(curr); walk(map, map(curr))
}
