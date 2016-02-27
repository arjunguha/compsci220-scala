sealed trait Expr {

  override def toString(): String = this match {
    case EInt(n) => n.toString
    case EBool(b) => b.toString
    case EAdd(e1, e2) => s"$e1 + $e2"
    case ELT(e1, e2) => s"$e1 < $e2"
    case EIf(e1, e2, e3) => s"if ($e1) { $e2 } else { $e3 }"
  }
}

case class EInt(n: Int) extends Expr
case class EBool(b: Boolean) extends Expr
case class EAdd(e1: Expr, e2: Expr) extends Expr
case class ELT(e1: Expr, e2: Expr) extends Expr
case class EIf(e1: Expr, e2: Expr, e3: Expr) extends Expr

sealed trait Type
case object TInt extends Type
case object TBool extends Type

object TypeError extends RuntimeException("Type error")

object TypeChecker {

  def tc(expr: Expr): Type = expr match {
    case EInt(_) => TInt
    case EBool(_) => TBool
    case EAdd(e1, e2) => (tc(e1), tc(e2)) match {
      case (TInt, TInt) => TInt
      case _ => throw TypeError
    }
    case ELT(e1, e2) => (tc(e1), tc(e2)) match {
      case (TInt, TInt) => TBool
      case _ => throw TypeError
    }
    case EIf(e1, e2, e3) => (tc(e1), tc(e2), tc(e3)) match {
      case (TBool, t1, t2) => {
        if (t1 == t2) {
          t1
        }
        else {
          throw TypeError
        }
      }
      case _ => throw TypeError
    }
  }
}