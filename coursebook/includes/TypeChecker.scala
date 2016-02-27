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

sealed trait Type
case object TInt extends Type
case object TBool extends Type

object TypeChecker {
  def tc(env: Map[String, Type], expr: Expr): Type = expr match {
    case EInt(_) => TInt
    case EBool(_) => TBool
    case EAdd(e1, e2) => (tc(env, e1), tc(env, e2)) match {
      case (TInt, TInt) => TInt
      case _ => throw new Exception("Type Error")
    }
    case ELT(e1, e2) => (tc(env, e1), tc(env, e2)) match {
      case (TInt, TInt) => TBool
      case _ => throw new Exception("Type Error")
    }
    case EIf(e1, e2, e3) => (tc(env, e1), tc(env, e2), tc(env, e3)) match {
      case (TBool, t1, t2) => {
        if (t1 == t2) {
          t1
        }
        else {
          throw new Exception("Type Error")
        }
      }
      case _ => throw new Exception("Type Error")
    }
    case EVal(x, e1, e2) => tc(env + (x -> tc(env, e1)), e2)
    case EId(x) => env.getOrElse(x, throw new Exception("Free variable"))
  }
}