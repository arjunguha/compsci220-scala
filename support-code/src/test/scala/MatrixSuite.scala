import org.scalatest.{FunSuite, Matchers}
import cmpsci220.hw.tictactoe._

class MatrixSuite extends FunSuite with Matchers{

  test("two sparse matrices are equal, even if set differently") {
    val m1 = Matrix(10, 0).set(1, 1, 10).set(0, 0, 20)
    val m2 = Matrix(10, 0).set(0, 0, 20).set(1, 1, 10)
    assert(m1 == m2)
  }

  test("matrix dimension considered in equality") {
    val m1 = Matrix(10, 0)
    val m2 = Matrix(20, 0)
    assert(m1 != m2)
  }


}