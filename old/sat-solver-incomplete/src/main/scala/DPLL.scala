sealed trait Expr {
  def &&(other: Expr): Expr = And(this, other)
  def ||(other: Expr): Expr = Or(this, other)
  def unary_!(): Expr = Not(this)
  def implies(other: Expr): Expr = Or(Not(this), other)
  def ^(other: Expr): Expr = Or(And(this, Not(other)), And(Not(this), other))
}
case class Var(name: String) extends Expr
case class Const(b: Boolean) extends Expr
case class Not(e: Expr) extends Expr
case class And(e1: Expr, e2: Expr) extends Expr
case class Or(e1: Expr, e2: Expr) extends Expr

object And {
  def apply(exprs: Expr*) = exprs.foldLeft(Const(true) : Expr) (_ && _)
}

object Or {
  def apply(exprs: Expr*) = exprs.foldLeft(Const(false) : Expr) (_ || _)
}

sealed trait Term[T] {
  def subst(x: String, v: Boolean): T
  def getVar(): Option[String]
  def getConst(): Option[Boolean]
  def ||(other: T): T
  def unary_!(): T
}

sealed trait Clause extends Term[Clause] {
  val isUnitClause: Boolean
  val literals: Map[String, Boolean]
}

sealed trait CNF extends Term[CNF] {
  def &&(other: CNF): CNF
  val sub: Map[String, Boolean]
}


case class ConstClause(b: Boolean) extends Clause {
  val isUnitClause = false
  val literals = Map[String, Boolean]()
  def getVar() = None
  def unary_!(): Clause = ConstClause(!b)
  def ||(other: Clause): Clause = if (b) this else other
  def subst(x: String, v: Boolean): Clause = this
  def getConst(): Option[Boolean] = Some(b)
}

// Represents a disjunction of literals
case class VarClause(val literals: Map[String, Boolean]) extends Clause {
  require (literals.size > 0)

  val isUnitClause = literals.size == 1

  override def toString(): String = {
    literals.toSeq.map({case (x, b) => if (b) s"!$x" else x }).mkString(" || ")
  }

  def unary_!(): Clause = VarClause(literals.mapValues(b => !b))

  def ||(other: Clause): Clause = other match {
    case ConstClause(b) => if (b) other else this
    case VarClause(otherLits) => {
      val lits = (this.literals.keySet ++ otherLits.keySet).foldLeft(Map[String, Boolean]()) {
        case (acc, x) => (this.literals.get(x), otherLits.get(x)) match {
          case (None, Some(b)) => acc + (x -> b)
          case (Some(b), None) => acc + (x -> b)
          case (Some(b1), Some(b2)) => if (b1 == b2) acc + (x -> b1) else acc
          case (None, None) => ???
        }
      }
      if (lits.isEmpty) ConstClause(false) else VarClause(lits)
    }
  }

  def subst(x: String, v1: Boolean): Clause = literals.get(x) match {
    case Some(v2) => {
      if (v1 == v2) {
        if (literals.size == 1) ConstClause(true) else VarClause(literals - x)
      }
      else {
        if (literals.size == 1) ConstClause(false) else VarClause(literals - x)
      }
    }
    case None => this
  }

  def getVar() = Some(literals.head._1)
  def getConst(): Option[Boolean] = None
}


case class ConstCNF(b: Boolean, sub: Map[String, Boolean]) extends CNF {
  def unary_!(): CNF = ConstCNF(!b, sub)
  def &&(other: CNF): CNF = if (b) other else this
  def ||(other: CNF): CNF = if (b) this else other
  def subst(x: String, v: Boolean): CNF = new ConstCNF(b, sub + (x -> v))
  def getVar() = None
  def getConst() = Some(b)
}


case class Conjuncts(clauses: List[Clause], sub: Map[String, Boolean]) extends CNF {

  require(clauses.isEmpty == false)

  def unary_!() = new Conjuncts(clauses.map(x => !x), sub)

  def &&(other: CNF) = other match {
    case ConstCNF(b, _) => if (b) this else other
    case Conjuncts(clauses2, sub2) => Conjuncts(this.clauses ++ clauses2, this.sub ++ sub2)
  }

  def ||(other: CNF): CNF = other match {
    case ConstCNF(b, _) => if (b) other else this
    case Conjuncts(clauses2, sub2) => {
      def disjunct(clauses1: List[Clause], clauses2: List[Clause]): List[Clause] = clauses1 match {
        case Nil => Nil
        case hd :: tl => clauses2.map(clause => hd || clause) ++ disjunct(tl, clauses2)
      }
      val clauses_ = disjunct(this.clauses, clauses2).filter(_.getConst != Some(true))
      if (clauses_.exists(_.getConst == Some(false))) {
        ConstCNF(false, sub)
      }
      else if (clauses_.isEmpty) {
        ConstCNF(true, sub)
      }
      else {
        Conjuncts(clauses_, this.sub ++ sub2)
      }
    }
  }

  def subst(x: String, v: Boolean): CNF = {
    def loop(clauses: List[Clause]): Option[List[Clause]] = clauses match {
      case Nil => Some(Nil)
      case head :: tail => head.subst(x, v) match {
        case ConstClause(true) => loop(tail)
        case ConstClause(false) => None
        case clause@VarClause(_) => loop(tail).map(t => clause :: t)
      }
    }
    loop(clauses) match {
      case None => ConstCNF(false, sub + (x -> v))
      case Some(Nil) => ConstCNF(true, sub + (x -> v))
      case Some(clauses_) => Conjuncts(clauses_, sub + (x -> v))
    }
  }

  def getVar() = clauses.head.getVar
  def getConst() = None

  override def toString(): String = clauses.map(x => s"($x)").mkString(" && ")

}

object Solution {

  def assign(expr: Expr, x: String, b: Boolean): Expr = expr match {
    case Var(y) => if (x == y) { Const(b) } else { expr }
    case Const(_) => expr
    case Not(e) => !(assign(expr, x, b))
    case And(e1, e2) => assign(e1, x, b) && assign(e2, x, b)
    case Or(e1, e2) => assign(e1, x, b) || assign(e2, x, b)
  }

  def vars(expr: Expr): Set[String] = expr match {
    case Const(_) => Set()
    case Var(x) => Set(x)
    case Not(e) => vars(e)
    case And(e1, e2) => vars(e1) union vars(e2)
    case Or(e1, e2) => vars(e1) union vars(e2)
  }

  def solveRec(expr: Expr, vars: List[String]): Option[Map[String, Boolean]] = {
    expr match {
      case Const(_) => Some(Map())
      case _ => vars match {
        case Nil => Some(Map())
        case x :: rest => {
          solveRec(assign(expr, x, true), rest) match {
            case Some(solution) => Some(solution)
            case None => solveRec(assign(expr, x, false), rest)
          }
        }
      }
    }
  }

  def solve(expr: Expr) = solveRec(expr, vars(expr).toList)

  def toCNF(expr: Expr): CNF = expr match {
    case Const(b) => ConstCNF(b, Map())
    case Var(x) => Conjuncts(List(VarClause(Map(x -> true))), Map())
    case Not(e) => !toCNF(e)
    case And(e1, e2) => toCNF(e1) && toCNF(e2)
    case Or(e1, e2) => toCNF(e1) || toCNF(e2)
  }

  def findUnits(cnf: Conjuncts): Map[String, Boolean] = {
    cnf.clauses.filter(_.isUnitClause).map(_.asInstanceOf[VarClause].literals.head).toMap
  }

  def findPureLits(cnf: List[Clause], acc: Map[String, Option[Boolean]]): Map[String, Boolean] = cnf match {
    case Nil => acc.filter(p => p._2.isDefined).mapValues(_.get)
    case head :: rest => {
      val acc_ = head.literals.foldLeft(acc) {
        case (acc, (x, b)) => acc.get(x) match {
          case Some(Some(b2)) => if (b == b2) acc else acc + (x -> None)
          case Some(None) => acc
          case None => acc + (x -> Some(b))
        }
      }
      findPureLits(rest, acc_)
    }
  }

  def pureLits(cnf: CNF) = cnf match {
    case ConstCNF(_, _) => Map()
    case Conjuncts(clauses, _) => findPureLits(clauses, Map())
  }


  def substAll(vars: List[(String, Boolean)], cnf: CNF): CNF = vars match {
    case Nil => cnf
    case (x,b) :: tail => substAll(tail, cnf.subst(x, b))
  }

  def dpll(phi: CNF): Option[Map[String, Boolean]] = phi match {
    case ConstCNF(true, sub) => Some(sub)
    case ConstCNF(false, _) => None
    case Conjuncts(clauses, sub) => {
      println(phi)
      val units = clauses.filter(_.isUnitClause).map(_.literals.head)
      println("Unit clauses are " + units)
      val phi1 = substAll(units, phi)
      println(phi1)
      val pure = pureLits(phi1)
      println("Pure literals are " + pure)
      val phi2 = substAll(pure.toList, phi1)
      println(phi2)
      phi2.getVar match {
        case Some(x) => dpll(phi2.subst(x, true)).orElse(dpll(phi2.subst(x, false)))
        case None => dpll(phi2)
      }
    }
  }

  def fastSolve(phi: Expr) = {
    println("formula:\n" + phi)
    val f = toCNF(phi)
    println("CNF:")
    println(f)
    println("solving...")
    dpll(f)
  }
}
