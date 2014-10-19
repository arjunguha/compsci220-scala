import cmpsci220.hw.parsing._
import scala.util.parsing.combinator._

object ArithParser extends ArithParserLike {

  lazy val atom: PackratParser[Expr] = {
    number ^^ { n => Num(n) } |
    "(" ~ expr ~ ")" ^^ { case "(" ~ e ~ ")" => e }
  }

  lazy val add: PackratParser[Expr] = {
    atom ~ "+" ~ add ^^ { case e1 ~ "+" ~ e2 => Add(e1, e2) } |
    atom ~ "-" ~ add ^^ { case e1 ~ _ ~ e2 => Sub(e1, e2) } |
    atom
  }

  lazy val mul: PackratParser[Expr] = {
    add ~ "*" ~ mul ^^ { case e1 ~ "*" ~ e2 => Mul(e1, e2) } |
    add ~ "/" ~ mul ^^ { case e1 ~ _ ~ e2 => Div(e1, e2) } |
    add
  }

  lazy val expr: PackratParser[Expr] = mul

}

object ArithPrinter extends ArithPrinterLike {

  def print(e: Expr): String = e match {
    case Num(n) => n.toString
    case Add(e1, e2) => "(" + print(e1) + " + " + print(e2) + ")"
    case Sub(e1, e2) => "(" + print(e1) + " - " + print(e2) + ")"
    case Mul(e1, e2) => "(" + print(e1) + " * " + print(e2) + ")"
    case Div(e1, e2) => "(" + print(e1) + " / " + print(e2) + ")"
  }

}

object ArithEval extends ArithEvalLike {

  def eval(e: Expr): Double = e match {
    case Num(n) => n
    case Add(e1, e2) => eval(e1) + eval(e2)
    case Sub(e1, e2) => eval(e1) - eval(e2)
    case Mul(e1, e2) => eval(e1) * eval(e2)
    case Div(e1, e2) => eval(e1) / eval(e2)
  }

}