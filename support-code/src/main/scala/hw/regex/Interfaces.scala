package cmpsci220.hw.regex

sealed trait Regex
case object One extends Regex
case object Zero extends Regex
case class Character(ch: Char) extends Regex
case class Alt(lhs: Regex, rhs: Regex) extends Regex
case class Seq(lhs: Regex, rhs: Regex) extends Regex
case class Star(re: Regex) extends Regex
