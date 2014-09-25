package edgemaze

sealed trait Direction
object North extends Direction
object South extends Direction
object East extends Direction
object West extends Direction

package object Graph {
    type Node = Int
    type Edge = (Node, Node, Int, Direction)
}
