import hw.parsing._
import org.scalatest._

import ArithParser._
import ArithPrinter._

class TestSuite extends FunSuite with prop.GeneratorDrivenPropertyChecks {

  test("parse number") {
    assert(parseAll(expr, "12").get == Num(12))
  }

  test("parse addition") {
    assert(parseAll(expr, "1 + 2").get == Add(Num(1), Num(2)))
  }

  test("parse subtraction") {
    assert(parseAll(expr, "1 - 2").get == Sub(Num(1), Num(2)))
  }

  test("parse multiplication") {
    assert(parseAll(expr, "1 * 2").get == Mul(Num(1), Num(2)))
  }


  test("parse precedence") {
    assert(parseAll(expr, "1 + 2 * 3 + 4").get ==
           Mul(Add(Num(1), Num(2)), Add(Num(3), Num(4))))
  }

  test("parse and pretty are related") {
    forAll(GenExpr.genExpr) { (e: Expr) =>
      val e1 = parseArith(print(e))
      assert(e == e1) // because parens are inserted naively
      assert(parseArith(print(e1)) == e1)
    }
  }

}