import scala.util.parsing.combinator._

sealed trait Expr
case class Num(n: Double) extends Expr
case class Add(e1: Expr, e2: Expr) extends Expr
case class Sub(e1: Expr, e2: Expr) extends Expr
case class Mul(e1: Expr, e2: Expr) extends Expr
case class Div(e1: Expr, e2: Expr) extends Expr

trait SolutionLike {

  def parse(s: String): Expr

  def eval(e: Expr): Double

  def print(e: Expr): String

}

object ArithParser extends RegexParsers with PackratParsers {

  lazy val number: PackratParser[Double] = """-?\d+(\.\d*)?""".r ^^ { _.toDouble }

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

  def parseArith(s: String) = parseAll(expr, s).get

}


object ArithPrinter {

  def print(e: Expr): String = e match {
    case Num(n) => n.toString
    case Add(e1, e2) => "(" + print(e1) + " + " + print(e2) + ")"
    case Sub(e1, e2) => "(" + print(e1) + " - " + print(e2) + ")"
    case Mul(e1, e2) => "(" + print(e1) + " * " + print(e2) + ")"
    case Div(e1, e2) => "(" + print(e1) + " / " + print(e2) + ")"
  }

}

object Eval {

  def eval(e: Expr): Double = e match {
    case Num(n) => n
    case Add(e1, e2) => eval(e1) + eval(e2)
    case Sub(e1, e2) => eval(e1) - eval(e2)
    case Mul(e1, e2) => eval(e1) * eval(e2)
    case Div(e1, e2) => eval(e1) / eval(e2)
  }

}

object Solution extends SolutionLike {

  def parse(s: String) = ArithParser.parseArith(s)

  def print(e: Expr) = ArithPrinter.print(e)

  def eval(e: Expr) = Eval.eval(e)

}
