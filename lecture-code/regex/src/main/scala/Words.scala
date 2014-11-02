package cmpsci220.regex

object Words {


  def words(re: Regex): Stream[List[Char]] = re match {
    case One => Stream(List())
    case Zero => Stream()
    case Character(ch) => Stream(List(ch))
    case Alt(lhs, rhs) => words(lhs) #::: words (rhs)
    case Seq(lhs, rhs) => {
      for (w1 <- words(lhs); w2 <- words(rhs)) yield {
        w1 ++ w2
      }
    }
    case Star(re) => words(Alt(One, Seq(re, Star(re))))
  }

}