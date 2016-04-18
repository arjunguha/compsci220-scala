class Tests extends org.scalatest.FunSuite {

  import Solution._

  test("DPLL 1") {

    val e = (!Var("y") && Var("z")) || Var("x")
    println(fastSolve(e))

  }

  test("DPLL 2") {

    val e = Var("x") && !Var("x")
    println(fastSolve(e))

  }

  test("n-queens") {

    println(NQueens.solve(4))

  }

}