import hw.parsing._
import scala.util.parsing.combinator._

object ArithEval extends ArithEvalLike {
  def eval(e: Expr): Double = ???
}

object ArithParser extends ArithParserLike {

  // number: PackratParser[Double] is defined in ArithParserLike

  lazy val atom: PackratParser[Expr] = ???

  lazy val exponent: PackratParser[Expr] = ???

  lazy val add: PackratParser[Expr] = ???

  lazy val mul: PackratParser[Expr] = ???

  lazy val expr: PackratParser[Expr] = ???
}

object ArithPrinter extends ArithPrinterLike {
  def print(e: Expr): String = ???
}