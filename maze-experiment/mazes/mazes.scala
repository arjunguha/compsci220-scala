package maze
// 6 x 6:
// S#####
// .....#
// #.####
// #.####
// ...#.F
// ##...#
sealed trait MazeNode
case class Vertex(idx: Int) extends MazeNode
case class Start(idx: Int) extends MazeNode
case class Finish(idx: Int) extends MazeNode
case class Wall() extends MazeNode

class Maze(width: Int, height: Int, mazestr: String) {
    val maze: List[MazeNode] = mazestr.zipWithIndex.map((t) => t._1 match {
        case '.' => Vertex(t._2)
        case 'S' => Start(t._2)
        case 'F' => Finish(t._2)
        case '#' => Wall()
    }).toList
    val w = width
    val h = height

    private val startIdx: Int =
        maze.find{case Start(v) => true; case _ => false} match {
            case Some(Start(v)) => v
            case _ => sys.error("Maze was created without a start location")
        }
    val start = maze(startIdx)

    private def north(v: Int) = v - w
    private def east(v: Int)  = if (v+1 % h == 0) -1 else v+1
    private def west(v: Int)  = if (v % h == 0) -1 else v-1
    private def south(v: Int) = v + w
    // Extractor object for matching Vertex, Start and Finish
    private object ValidNode {
        def unapply(m: MazeNode) = m match {
            case Wall() => None
            case Vertex(v) => Some(v)
            case Start(v) => Some(v)
            case Finish(v) => Some(v)
        }
    }
    // Returns the indices in the cardinal directions. If this would be outside
    // the maze, that value is -1.
    def adjacentIndices(m: Int): List[Int] = {
        if (maze isDefinedAt m) {
            maze(m) match {
                case Wall() => List(-1, -1, -1, -1)
                case ValidNode(v) =>
                    List(north(v), east(v), south(v), west(v))
            }
        } else { List(-1, -1, -1, -1) }
    }

    def adjacentVertices(m: MazeNode): List[MazeNode] = m match {
        case Wall() => List(Wall(), Wall(), Wall(), Wall())
        case ValidNode(v) =>
            adjacentIndices(v).map((i) => maze.applyOrElse(i,(x: Int) => Wall()))
    }

    override def toString = "You can't just LOOK at a maze!"
}

object Maze {
    def apply(w: Int, h: Int, str: String) = new Maze(w, h, str)
}


