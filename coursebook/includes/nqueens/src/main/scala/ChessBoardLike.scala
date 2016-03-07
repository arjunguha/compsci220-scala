trait ChessBoardLike {
  val dim: Int
  val solution: Set[(Int, Int)]

  override def toString(): String = {
    val builder = new StringBuilder((dim + 1) * dim)
    for (y <- 0.to(dim - 1)) {
      for (x <- 0.to(dim - 1)) {
        if (solution.contains((x, y))) { builder += 'Q' }
        else { builder += '.' }
      }
      builder ++= "\n"
    }
    builder.toString
  }
}
