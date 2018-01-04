class TrivialTestSuite extends org.scalatest.FunSuite {

  test("Parser and Printer have the right types") {
    val parser: hw.json.JSONParserLike = JSONParser
    val printer: hw.json.JSONPrinterLike = JSONPrinter
  }

}
