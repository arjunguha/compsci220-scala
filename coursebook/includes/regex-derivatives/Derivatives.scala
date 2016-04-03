object RegexDerivs {

  val re_scala = "cs2(2|3)0".r

  val re = Seq(Character('c'), Seq(Character('s'),
                Seq(Character('2'), Seq(Alt(Character('2'), Character('3')), Character('0')))))

  val ex1 = "(ab)c"
  val ex1_alt = "a(bc)"

  def empty(re: Regex): Regex = re match {
    case Character(_) => Zero
    case Alt(re1, re2) => empty(re1) + empty(re2)
    case Seq(re1, re2) => empty(re1) >> empty(re2)
    case Star(_) => One
    case One => One
    case Zero => Zero
  }

  def deriv(regex: Regex, ch: Char): Regex = regex match {
    case Character(ch_) => if (ch == ch_) One else Zero
    case One => Zero
    case Zero => Zero
    case Alt(re1, re2) => deriv(re1, ch) + deriv(re2, ch)
    case Seq(re1, re2) => (deriv(re1, ch) >> re2) + (empty(re1) >> deriv(re2, ch))
    case Star(r) => deriv(r, ch) >> Star(r)
  }

  def reMatch(regex: Regex, str: List[Char]): Boolean = str match {
    case Nil => empty(regex) == One
    case ch :: rest => reMatch(deriv(regex, ch), rest)
  }

  // If time
  def chars(re: Regex): Set[Char] =re match {
    case Character(ch) => Set(ch)
    case Alt(re1, re2) => chars(re1) union chars(re2)
    case Seq(re1, re2) => chars(re1) union chars(re2)
    case Star(r) => chars(r)
    case One => Set()
    case Zero => Set()
  }

}