package edgemaze

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
