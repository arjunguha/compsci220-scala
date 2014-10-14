package cmpsci220.hw.graph


/**
 * An undirected graph with labelled nodes and edges and no self-loops.
 */
class Graph[Node, Edge]() {

  private class Vx(theNode : Node) extends Vertex[Vx, Edge] {
    val node = theNode
  }

  private val vertices = collection.mutable.Map[Node, Vx]()

  /**
   * Creates a new node in the graph. Returns {@code false} if the node
   * already exists. Returns {@code true} if the node did not exist and
   * was just created.
   */
  def mkNode(node: Node): Boolean = vertices.get(node) match {
    case Some(vx) => false
    case None => {
      val vx = new Vx(node)
      vertices += (node -> vx)
      true
    }
  }

  /**
   * Creates an edge between {@code node1} and {@code node2} labelled
   * {@code edge}. Returns {@code true} if the edge was succesfully created.
   * Returns {@code false} if the edge could not be created.
   *
   * There are several reasons why an edge may not be created successfully:
   * (1) Either node may not exist in the graph. (2) {@code node1} and
   * {@code node2} may be the same node, but no self-loops are allowed.
   * (3) There may already be an edge between the two nodes.
   */
  def mkEdge(node1: Node, edge: Edge, node2: Node): Boolean = {
    (vertices.get(node1), vertices.get(node2)) match {
      case (Some (vx1), Some(vx2)) => vx1.mkEdge(edge, vx2)
      case _ => false
    }    
  }

  def rmEdge(node1: Node, node2: Node): Boolean = {
    (vertices.get(node1), vertices.get(node2)) match {
      case (Some (vx1), Some(vx2)) => vx1.rmEdge(vx2)
      case _ => false
    }
  }

  /**
   * Removes a node from the graph. Returns {@code true} if the node
   * is successsfully removed, or {@code false} if the node did not exist.
   */
  def rmNode(node: Node): Boolean = vertices.get(node) match {
    case None => false
    case Some(vx) => {
      for ((other, _) <- vx.edges) {
        vx.rmEdge(other)
      }
      vertices -= node
      true
    }
  }

  def nodes(): Seq[Node] = vertices.keys.toSeq

  /**
   * Returns the set of neighboring vertices of {@code node}. Returns
   * the empty set if {@code node} is not a vertex in the graph.
   */
  def neighbors(node: Node): Set[Node] = vertices.get(node) match {
    case None => Set()
    case Some(vx) => vx.edges.keys.map(_.node).toSet
  }


  /**
   * Returns the label on the edge between {@code node1} and {@code node2}.
   * Throws {@code IllegalArgumentException} if {@code node1} and {@code node2}
   * are not neighbors.
   */
  def getEdge(node1: Node, node2: Node): Edge = {
    val r = for {
      vx1 <- vertices.get(node1)
      vx2 <- vertices.get(node2)
      edge <- vx1.edges.get(vx2)
    } yield (edge)
    r.getOrElse(throw new IllegalArgumentException("not neighbors"))
  }

}

object Graph {

  /**
   * Creates a graph from a list of edges.
   */
  def apply[Node, Edge](edges: (Node, Edge, Node)*) = {
    val graph = new Graph[Node, Edge]()
    for ((node1, edge, node2) <- edges) {
      graph.mkNode(node1)
      graph.mkNode(node2)
      graph.mkEdge(node1, edge, node2)
    }
    graph
  }

}