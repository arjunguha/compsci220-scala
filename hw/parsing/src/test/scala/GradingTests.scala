class GradingTests extends org.scalatest.FunSuite {
    import hw.parsing._
    import ArithParser.parseArith
    import ArithEval.eval
    import ArithPrinter.print

    def testPP(exp: Expr): Unit = {
      val r = parseArith(print(exp))
      assert(r == exp ||  eval(r) == eval(exp))
    }

    def testP(str: String, exp: Expr, result: Double): Unit = {
      val r = parseArith(str)
      assert(r == exp || eval(r) == result)
      }

    test ("Can parse and eval 1000") {
       testP("1000", Num(1000), 1000)
    }

    test("Can parse and eval 2 + 3") {
       testP("2 + 3", Add(Num(2), Num(3)), 5)
    }
    test("10 * 5") {testP("10 * 5", Mul(Num(10), Num(5)), 50)}

    test("2 * 3 + 4") {
      testP("2 * 3 + 4", Add(Mul(Num(2), Num(3)), Num(4)), 10)
    }

    test("2 + 3 * 4") {
      testP("2 + 3 * 4", Add(Num(2), Mul(Num(3), Num(4))), 14)
    }
    test("(2 + 3) * 4") {
      testP("(2 + 3) * 4", Mul(Add(Num(2), Num(3)), Num(4)), 20)
    }

    test("2 + (3 * 4)") {
      testP("2 + (3 * 4)", Add(Num(2), Mul(Num(3), Num(4))), 14)
    }

    test("print.parse sum") {
      testPP(Add(Num(2), Num(3)))
    }
    test("print.parse prod") {
      testPP(Mul(Num(2), Num(3)))
    }
    test("print.parse precedence") {
      testPP(Add(Mul(Num(2), Num(3)), Num(4)))
    }

    test("print.parse precedence (parens required)") {
      testPP(Mul(Add(Num(2), Num(3)), Num(4)))
    }

    test("eval number") {
      assert(eval(Num(3)) == 3)
    }

    test("eval sub") {
      assert(eval(Sub(Num(3), Num(10))) == -7)}

    test("eval div") {
      assert(eval(Div(Num(10), Num(2))) == 5)
      }

    test("eval nested") {
      assert(eval(Add(Num(2), Mul(Num(3), Num(4)))) == 14)
    }

}