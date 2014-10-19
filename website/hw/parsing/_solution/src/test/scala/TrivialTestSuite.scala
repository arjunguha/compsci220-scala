class TrivialTestSuite extends org.scalatest.FunSuite {

  test("several objects must be defined") {
    val parser: cmpsci220.hw.parsing.ArithParserLike = ArithParser
    val printer: cmpsci220.hw.parsing.ArithPrinterLike = ArithPrinter
    val eval: cmpsci220.hw.parsing.ArithEvalLike = ArithEval
  }


}
