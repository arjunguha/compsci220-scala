object AbstractVertex {

  class Vertex(label: String) {

    private val succs = scala.collection.mutable.Map[Vertex, Int]()
    private val preds = scala.collection.mutable.Map[Vertex, Int]()

    def addEdge(to: Vertex, edgeLabel: Int): Unit = {
      assert(to != this)
      assert(this.succs.contains(to) == false)
      this.succs += to -> edgeLabel
      to.preds += this -> edgeLabel
    }

    def successors(): List[Vertex] = succs.keys.toList
  }

  trait VertexLike[A] {
    def addEdge(to: A, edgeLabel: Int): A
    def successors(): List[A]
  }

  class Graph[V <: Vertex[V], E]() {

    private val vxs = scala.collection.mutable.Set[V]()

    def newVertex(label: String): Vertex = new Vertex(label)

    private def dfs(visited: Set[Vertex], fringe: List[Vertex]): List[Vertex] = {
      fringe match {
        case Nil => Nil
        case hd :: tl => {
          if (visited.contains(hd)) {
            dfs(visited, tl)
          }
          else {
            dfs(visited + hd, fringe ++ hd.successors())
          }
        }
      }
    }

    def depthFirstTraversal(root: Vertex): List[Vertex] = dfs(Set(), List(root))

  }


}
object NaiveGraph {

  class Vertex(label: String) {

    private val succs = scala.collection.mutable.Map[Vertex, Int]()
    private val preds = scala.collection.mutable.Map[Vertex, Int]()

    def addEdge(to: Vertex, edgeLabel: Int): Unit = {
      assert(to != this)
      assert(this.succs.contains(to) == false)
      this.succs += to -> edgeLabel
      to.preds += this -> edgeLabel
    }

    def successors(): List[Vertex] = succs.keys.toList
  }

  class Graph() {

    private val vxs = scala.collection.mutable.Set[Vertex]()

    def newVertex(label: String): Vertex = new Vertex(label)

    private def dfs(visited: Set[Vertex], fringe: List[Vertex]): List[Vertex] = {
      fringe match {
        case Nil => Nil
        case hd :: tl => {
          if (visited.contains(hd)) {
            dfs(visited, tl)
          }
          else {
            dfs(visited + hd, fringe ++ hd.successors())
          }
        }
      }
    }

    def depthFirstTraversal(root: Vertex): List[Vertex] = dfs(Set(), List(root))

  }
}
