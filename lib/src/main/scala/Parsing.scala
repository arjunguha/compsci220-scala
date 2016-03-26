package hw.parsing

import scala.util.parsing.combinator._

sealed trait Expr
case class Num(n: Double) extends Expr
case class Add(e1: Expr, e2: Expr) extends Expr
case class Sub(e1: Expr, e2: Expr) extends Expr
case class Mul(e1: Expr, e2: Expr) extends Expr
case class Div(e1: Expr, e2: Expr) extends Expr
case class Exponent(e1: Expr, e2: Expr) extends Expr

trait ArithParserLike extends RegexParsers with PackratParsers {
  lazy val number: PackratParser[Double] = """-?\d+(\.\d*)?""".r ^^ { _.toDouble }
  val atom: PackratParser[Expr]
  val exponent: PackratParser[Expr]
  val add: PackratParser[Expr]
  val mul: PackratParser[Expr]
  val expr: PackratParser[Expr]

  def parseArith(s: String) = parseAll(expr, s).get
}

trait ArithPrinterLike {
  def print(e: Expr): String
}

trait ArithEvalLike {
  def eval(e: Expr): Double
}