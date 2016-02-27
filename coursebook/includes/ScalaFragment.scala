sealed trait Expr {

  override def toString(): String = this match {
    case EInt(n) => n.toString
    case EBool(b) => b.toString
    case EAdd(e1, e2) => s"$e1 + $e2"
    case ELT(e1, e2) => s"$e1 < $e2"
    case EIf(e1, e2, e3) => s"if ($e1) { $e2 } else { $e3 }"
    case EVal(x, e1, e2) => s"val $x = $e1\n$e2"
    case EId(x) => x
  }
}

case class EInt(n: Int) extends Expr
case class EBool(b: Boolean) extends Expr
case class EAdd(e1: Expr, e2: Expr) extends Expr
case class ELT(e1: Expr, e2: Expr) extends Expr
case class EIf(e1: Expr, e2: Expr, e3: Expr) extends Expr
case class EVal(x: String, e1: Expr, e2: Expr) extends Expr
case class EId(x: String) extends Expr
