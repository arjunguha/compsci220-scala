package cmpsci220.hw.graph

trait GraphAlgorithms {

  def reachable[Node, Edge](graph: Graph[Node, Edge], start: Node): Set[Node]

  def isValidPath[Node, Edge](graph: Graph[Node, Edge], path: List[Node]): Boolean

  def depthFirstSearch[Node, Edge](graph: Graph[Node, Edge], start: Node, stop: Node): Option[List[Node]]

  def breathFirstSearch[Node, Edge](graph: Graph[Node, Edge], start: Node, stop: Node): Option[List[Node]]

  def shortestPath[Node](graph: Graph[Node, Float], start: Node, stop: Node): Float

}