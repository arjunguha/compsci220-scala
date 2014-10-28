import scala.language.implicitConversions

sealed trait Regex {

  def |(other: Regex) = Alt(this, other)

  def $(other: Regex) = Seq(this, other)

}

case object Epsilon extends Regex
case object Z extends Regex
case class Character(ch: Char) extends Regex
case class Alt(lhs: Regex, rhs: Regex) extends Regex
case class Seq(lhs: Regex, rhs: Regex) extends Regex
case class Star(re: Regex) extends Regex

object Main extends App {

  implicit def charToRegex(ch: Char): Regex = Character(ch)

  def matches(re: Regex, str: List[Char]): Boolean = re match {
    case Epsilon => str.isEmpty
    case Z => false
    case Character(ch) => str == List(ch)
    case Alt(lhs, rhs) => matches(lhs, str) || matches(rhs, str)
    case Seq(lhs, rhs) => {
      for (n <- 0.to(str.length)) {
        val (str1, str2) = str.splitAt(n)
        if (matches(lhs, str1) && matches(rhs, str2)) {
          return true
        }
      }
      return false
    }
    case Star(re) => matches(Alt(Epsilon, re $ Star(re)), str)
  }

  println(matches(Seq(Star('a'), 'b'), "aaab".toList))

}