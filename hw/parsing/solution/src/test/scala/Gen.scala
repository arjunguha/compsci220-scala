import hw.parsing._
import org.scalacheck._
import Gen._
import Arbitrary.arbitrary

object GenExpr {

  private def genNum: Gen[Expr] = for {
    n <- choose(-10, 10)
  } yield Num(n)

  private def genSizedExpr(size: Int): Gen[Expr] = {
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
