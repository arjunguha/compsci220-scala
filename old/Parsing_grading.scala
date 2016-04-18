package cmpsci220.grading

import cmpsci220.hw.parsing._
import grader.TestSuite

class Parsing(targetYaml: String,
              ArithEval: ArithEvalLike,
              ArithParser: ArithParserLike,
              ArithPrinter: ArithPrinterLike) {

  import ArithParser.parseArith
  import ArithEval.eval
  import ArithPrinter.print

  TestSuite(targetYaml) { builder =>

    import builder._

    def testPP(desc: String, exp: Expr): Unit = {
      test(desc) {
        val r = parseArith(print(exp))
        assert(r == exp ||  eval(r) == eval(exp))
      }
    }

    def testP(str: String, exp: Expr, result: Double): Unit = {
      test(s"parse $str") {
        val r = parseArith(str)
        assert(r == exp || eval(r) == result)
      }
    }

    testP("1000", Num(1000), 1000)
    testP("2 + 3", Add(Num(2), Num(3)), 5)
    testP("10 * 5", Mul(Num(10), Num(5)), 50)
    testP("2 * 3 + 4", Add(Mul(Num(2), Num(3)), Num(4)), 10)
    testP("2 + 3 * 4", Add(Num(2), Mul(Num(3), Num(4))), 14)
    testP("(2 + 3) * 4", Mul(Add(Num(2), Num(3)), Num(4)), 20)
    testP("2 + (3 * 4)", Add(Num(2), Mul(Num(3), Num(4))), 14)
    testPP("print.parse sum", Add(Num(2), Num(3)))
    testPP("print.parse prod", Mul(Num(2), Num(3)))
    testPP("print.parse precedence", Add(Mul(Num(2), Num(3)), Num(4)))
    testPP("print.parse precedence (parens required)", Mul(Add(Num(2), Num(3)), Num(4)))

    test("eval number") {
      assert(eval(Num(3)) == 3)
    }

    test("eval sub") {
      assert(eval(Sub(Num(3), Num(10))) == -7)
    }

    test("eval div") {
      assert(eval(Div(Num(10), Num(2))) == 5)
    }

    test("eval nested") {
      assert(eval(Add(Num(2), Mul(Num(3), Num(4)))) == 14)
    }
  }
}
