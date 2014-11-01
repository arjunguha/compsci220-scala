package cmpsci220.regex

object Derivative {

  def alt(re1: Regex, re2: Regex): Regex = (re1, re2) match {
    case (Zero, Zero) => Zero
    case (Zero, q) => q
    case (p, Zero) => p
    case _ => Alt(re1, re2)
  }

  def seq(re1: Regex, re2: Regex): Regex = (re1, re2) match {
    case (Zero, q) => Zero
    case (p, Zero) => Zero
    case (One, q) => q
    case (p, One) => p
    case _ => Seq(re1, re2)
  }

  def containsEmpty(re: Regex): Regex = re match {
    case One => One
    case Zero => Zero
    case Character(_) => Zero
    case Alt(lhs, rhs) => alt(containsEmpty(lhs), containsEmpty(rhs))
    case Seq(lhs, rhs) => seq(containsEmpty(lhs), containsEmpty(rhs))
    case Star(_) => One
  }

  def next(char: Char, re: Regex): Regex = {
     re match {
    case One => Zero
    case Zero => Zero
    case Character(ch) => if (char == ch) One else Zero
    case Alt(lhs, rhs) => alt(next(char, lhs), next(char, rhs))
    case Seq(lhs, rhs ) => alt(seq(next(char, lhs), rhs),
                              seq(containsEmpty(lhs), next(char, rhs)))
    case Star(re) => next(char, Seq(re, Star(re)))
  } }

  def matches(re: Regex, str: List[Char]): Boolean = {
    println(re, str)
    str match {
      case Nil => containsEmpty(re) == One
      case ch :: rest => matches(next(ch, re), rest)
    }
  }

}