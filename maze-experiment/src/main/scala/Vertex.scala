// Relies on the .== method being object identity. i.e., do not
// use on a case class and do not override .==
// This is a whacky signature. But, all it really does it
// force you to write  "class X extends Vertex[X, E]"
// i.e., if you write "class X extends Vertex[Y, E]", you'll get
// a type error.
// For more information, lookup F-bounded polymorphism
//
trait Vertex[V <: Vertex[V,E], E] { self: V =>

  private val neighbors = collection.mutable.Map[V, E]()

  def edges(): Map[V, E] = neighbors.toMap

  // true if the edge is created. No self-loops. Undirected graph. At most
  // one edge between two vertices.
  def mkEdge(label: E, other: V): Boolean = {
    if (other == this) {
      false
    }
    else {
      neighbors.get(other) match {
        case None => {
          neighbors += (other -> label)
          other.mkEdge(label, this)
          true
        }
        case Some(_) => false
      }
    }
  }

  // true if the edge existed and was deleted.
  def rmEdge(other: V): Boolean = neighbors.get(other) match {
    case None => {
      false
    }
    case Some(_) => {
      neighbors -= other
      other.rmEdge(this)
      true
    }
  }

  def isNeighbor(other: V): Boolean = !neighbors.get(other).isEmpty

}
