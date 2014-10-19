import org.scalatest._

import ArithParser._
import ArithPrinter._

object ExprGenerator {

  import org.scalacheck._
  import Gen._
  import Arbitrary.arbitrary

  def genNum: Gen[Expr] = for {
    n <- choose(-10, 10)
  } yield Num(n)

  def genSizedExpr(size: Int): Gen[Expr] = {
    if (size == 0) {
      genNum
    }
    else {
      for {
        e1 <- genSizedExpr(size / 2)
        e2 <- genSizedExpr(size / 2)
        op <- oneOf(Add, Sub, Mul, Div)
      } yield op(e1, e2)
    }
  }

  def genExpr = sized(genSizedExpr)

}

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
    forAll(ExprGenerator.genExpr) { (e: Expr) =>
      val e1 = parseArith(print(e))
      assert(e == e1) // because parens are inserted naively
      assert(parseArith(print(e1)) == e1)
    }
  }

}