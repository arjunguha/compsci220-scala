import edgemaze._

sealed trait Command
case class TurnLeft() extends Command
case class TurnRight() extends Command
case class TurnAround() extends Command
case class GoForward(dist: Int) extends Command

class Driver() {
    // Driver starts off facing north
    private val dirs = List(North(), East(), South(), West())
    val initialDirection: Int = 0 // North
    private var dir: Int = initialDirection
    private var crashed: Boolean = false

    private def reinit() = {
        dir = initialDirection
        crashed = false
    }

    def direction(): Direction = dirs(dir)
    private def rotate(x: Int) = dir = (dir + x) % dirs.length

    private def turnLeft() = rotate(3)
    private def turnRight() = rotate(1)
    private def turnAround() = rotate(2)

    private def goForward(g: MazeGraph, curr: Graph.Node, dist: Int): Graph.Node =
        g.edges.find{ e =>
            e._1 == curr && e._3 == dist && e._4 == direction } match {
            case None => crashed = true; curr
            case Some((v, target, d, dir)) => target
        }

    def navigateMaze(g: MazeGraph, d: List[Command]): Boolean = {
        reinit
        nmh(g, d, g.start)
    }

    private def nmh(g: MazeGraph, d: List[Command], curr: Graph.Node): Boolean =
        if (this.crashed) {
            println("Crashed into a wall!")
            false
        } else if (d.isEmpty) {
            if (curr == g.finish) {
                println("Reached the end!")
                true
            } else {
                println("Ran out of instructions before reaching the end...")
                false
            }
        } else {
            val nextNode = d.head match {
                case TurnLeft() =>
                    println("Turned left.")
                    this.turnLeft
                    curr
                case TurnRight() =>
                    println("Turned right.")
                    this.turnRight
                    curr
                case TurnAround() =>
                    println("Turned around.")
                    this.turnAround
                    curr
                case GoForward(dist) =>
                    println(s"Moving $dist spaces.")
                    this.goForward(g, curr, dist)
            }
            nmh(g, d.tail, nextNode)
        }
}

object Driver {
    def apply() = new Driver()
}

val d = Driver()

val g: MazeGraph = {
    val start = 0
    val finish = 10
    new MazeGraph(
        List(start,1,2,3,4,5,6,7,8,9,finish),
        List(
            // Start
            (start, 1, 1, South()),
            // Node 1
            (1, start, 1, North()),
            (1, 2, 1, East()),
            // Node 2
            (2, 3, 3, East()),
            (2, 4, 3, South()),
            (2, 1, 1, West()),
            // Node 3
            (3, 2, 3, West()),
            // Node 4
            (4, 2, 3, North()),
            (4, 6, 1, East()),
            (4, 5, 1, West()),
            // Node 5
            (5, 4, 1, East()),
            // Node 6
            (6, 7, 1, South()),
            (6, 4, 1, West()),
            // Node 7
            (7, 6, 1, North()),
            (7, 8, 2, East()),
            // Node 8
            (8, 9, 1, North()),
            (8, 7, 2, West()),
            // Node 9
            (9, finish, 1, East()),
            (9, 8, 1, South()),
            // Finish
            (finish, 9, 1, West())),
        start, finish)
}

val c = List(
    // From S to 1
    TurnAround(),
    GoForward(1),
    // From 1 to 2
    TurnLeft(),
    GoForward(1),
    // From 2 to 4
    TurnRight(),
    GoForward(3),
    // From 4 to 6
    TurnLeft(),
    GoForward(1),
    // From 6 to 7
    TurnRight(),
    GoForward(1),
    // From 7 to 8
    TurnLeft(),
    GoForward(2),
    // From 8 to 9
    TurnLeft(),
    GoForward(1),
    // From 9 to F
    TurnRight(),
    GoForward(1))

def checkDir(fromD: Direction, toD: Direction): Option[Command] = {
    (fromD, toD) match {
        case (North(), East()) | (East(), South()) |
             (South(), West()) | (West(), North()) => Some(TurnRight())
        case (North(), South()) | (East(), West()) |
             (South(), North()) | (West(), East()) => Some(TurnAround())
        case (North(), West()) | (East(), North()) |
             (South(), East()) | (West(), South()) => Some(TurnLeft())
        case _ => None
    }
}


// Assume p is a list of adjacent nodes that lead from g.start to g.finish
def buildCommandList(g: MazeGraph, path: List[Graph.Node]): List[Command] = {
    val currDir = North()

    def bcl(g: MazeGraph, path: List[Graph.Node], d: Direction, curr: Graph.Node): List[Command]
    = path match {
        case Nil => Nil
        case head :: rest =>
            val edge = g.edgeBetween(curr, head).get
            checkDir(d, edge._4) match {
                case None => GoForward(edge._3) :: bcl(g, rest, edge._4, head)
                case Some(com) => com :: bcl(g, path, edge._4, curr)
            }
    }
    bcl(g, path.tail, currDir, path.head)
}

println(buildCommandList(g, List(g.start,1,2,4,6,7,8,9,g.finish)) == c)
// d.navigateMaze(g, c)
