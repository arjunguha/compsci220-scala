package cmpsci220.regex

sealed trait Regex
case object One extends Regex
case object Zero extends Regex
case class Character(ch: Char) extends Regex
case class Alt(lhs: Regex, rhs: Regex) extends Regex
case class Seq(lhs: Regex, rhs: Regex) extends Regex
case class Star(re: Regex) extends Regex

object Regex {

  def matches(re: Regex, str: List[Char]): Boolean = re match {
    case One => str.isEmpty
    case Zero => false
    case Character(ch) => str == List(ch)
    case Alt(lhs, rhs) => matches(lhs, str) || matches(rhs, str)
    case Seq(lhs, rhs) => {
      for (n <- util.Random.shuffle(0.to(str.length))) {
        val (str1, str2) = str.splitAt(n)
        if (matches(lhs, str1) && matches(rhs, str2)) {
          return true
        }
      }
      return false
    }
    case Star(re) => str.isEmpty || matches(Seq(re, Star(re)), str)
  }

  def matches(re: Regex, str: String): Boolean = matches(re, str.toList)

}