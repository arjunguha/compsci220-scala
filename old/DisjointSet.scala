package cmpsci220.hw.disjointset

class DisjointSet[A]() {

  private case class Vx(var parent: Vx) {
  }

  private val vertices = collection.mutable.Map[A, Vx]()

  private def findRep(vx: Vx): Vx = {
    if (vx.parent == null) {
      vx
    }
    else {
      findRep(vx.parent)
    }
  }

  private def vertexOf(node: A): Vx = {
    vertices.get(node) match {
      case (Some(vx)) => vx
      case None => {
        val vx = new Vx(null)
        vertices += node -> vx
        vx
      }
    }
  }

  def inSameSet(node1: A, node2: A): Boolean = {
    findRep(vertexOf(node1)).eq(findRep(vertexOf(node2)))
  }

  def union(node1: A, node2: A): Unit = {
    val node1Rep = findRep(vertexOf(node1))
    val node2Rep = findRep(vertexOf(node2))
    if (node1Rep.eq(node2Rep)) {
      // already in same set
    }
    else {
      node1Rep.parent = node2Rep
    }
  }

}
