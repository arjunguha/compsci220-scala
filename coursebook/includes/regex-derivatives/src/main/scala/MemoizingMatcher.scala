class MemoizingMatcher(regex: Regex) {

  private val derivs = scala.collection.mutable.Map[(Regex, Char), Regex]()

  def size(): Int = derivs.size

  def deriv(regex: Regex, ch: Char): Regex = derivs.get((regex, ch)) match {
    case Some(dRegex) => dRegex
    case None => {
      val dRegex = regex match {
        case Character(ch_) => if (ch == ch_) One else Zero
        case One => Zero
        case Zero => Zero
        case Alt(re1, re2) => deriv(re1, ch) | deriv(re2, ch)
        case Seq(re1, re2) =>
          (deriv(re1, ch) >> re2) | (RegexDerivs.empty(re1) >> deriv(re2, ch))
        case Star(r) => deriv(r, ch) >> Star(r)
      }
      derivs += (regex, ch) -> dRegex
      dRegex
    }
  }

  def reMatchRec(regex: Regex, str: List[Char]): Boolean = str match {
    case Nil => RegexDerivs.empty(regex) == One
    case ch :: rest => reMatchRec(deriv(regex, ch), rest)
  }

  def contains(str: String) = reMatchRec(regex, str.toList)

  def toDot(): String = {
    val builder = new StringBuilder()
    builder.append("digraph G {\n")
    val states = (derivs.keys.map(_._1) ++ derivs.values).toSet
      .toList.zip(Stream.from(0).map(n => "state" + n)).toMap
    for ((re, name) <- states) {
      builder.append(name)
      builder.append(""" [label="""")
      builder.append(re)
      builder.append("\"];\n")
    }
    for (((src, char), dst) <- derivs) {
      builder.append(states(src))
      builder.append(" -> ")
      builder.append(states(dst))
      builder.append(""" [label="""")
      builder.append(char)
      builder.append("\"];\n")
    }
    builder.append("}\n")
    builder.toString
  }

  /** Produces a file that can be viewed with GraphViz. */
  def saveDot(filename: String): Unit = {
    import java.nio.file._
    Files.write(Paths.get(filename), this.toDot().getBytes)
  }
}