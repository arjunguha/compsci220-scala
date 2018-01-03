class ParserTest extends org.scalatest.FunSuite {

  import JSON._
  import JParser._

  test("num") {
    assert(parse("1") == JsonNumber(1))
  }

  test("double") {
    assert(parse("1.1") == JsonNumber(1.1))
  }

  test("string") {
    val str = """ "this is a string" """
    assert(parse(str) === JsonString("this is a string"))
  }

  test("true") {
    assert(parse("true") === JsonBool(true))
  }

  test("false") {
    assert(parse("false") === JsonBool(false))
  }

  test("null") {
    assert(parse("null") === JsonNull())
  }

  test("object") {
    val obj = """ { "a" : 1 } """
    assert(parse(obj) === JsonDict(Map("a" -> JsonNumber(1))))
  }

  test("array") {
    val arr = """ [1, 2, 3, 4] """
    assert(parse(arr) === JsonArray(List(1,2,3,4).map(n => JsonNumber(n))))
  }

  test("array in obj") {
    val obj = """ {"a": [1,2,3]} """
    assert(parse(obj) === JsonDict(Map("a" -> JsonArray(
      List(1,2,3).map(n => JsonNumber(n))))))
  }
}
