import scala.util.parsing.combinator._

object ArithParser extends RegexParsers with PackratParsers {

  lazy val digit: PackratParser[_] = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"

  lazy val number: PackratParser[_] = (digit ~ number) | digit

  lazy val atom: PackratParser[_] = {
    number |
    "(" ~ expr ~ ")"
  }

  lazy val exponent: PackratParser[_] = {
    exponent ~ "^" ~ atom  |
    atom
  }

  lazy val add: PackratParser[_] = {
    add ~ "+" ~ exponent |
    add ~ "-" ~ exponent |
    exponent
  }

  lazy val mul: PackratParser[_] = {
    add ~ "*" ~ mul |
    add ~ "/" ~ mul |
    add
  }

  lazy val expr: PackratParser[_] = mul

}
