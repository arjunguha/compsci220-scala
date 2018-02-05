import Combinators._
import hw.json._

class TestSuite extends org.scalatest.FunSuite {

  val num = JsonNumber(1)
  val dict = JsonDict(Map(JsonString("a") -> num))
  val arr = JsonArray(List(num, JsonNumber(2), JsonNumber(3)))

  test("Does the number combinator work when input is JsonNumber") {
    assert(number.func(num) === List(1))
  }

  test("Does the number combinator work when input is not JsonNumber") {
    assert(number.func(arr) === Nil)
  }

  test("Does the key combinator work when input is JsonDict") {
    assert(key("a").func(dict) === List(num))
  }

  test("Does the key combinator work when input is not JsonDict") {
    assert(key("a").func(num) === Nil)
  }

  test("Does the index combinator work when input is JsonArray") {
    assert(index(0).func(arr) === List(num))
    assert(index(1).func(arr) === List(JsonNumber(2)))
  }

  test("Does the index combinator work when input is not JsonArray") {
    assert(index(1).func(dict) === Nil)
  }

  test("Does the iter combinator work when input is JsonArray") {
    assert(iter.func(arr) === List(num, JsonNumber(2), JsonNumber(3)))
  }

  test("Does the iter combinator work when input is not JsonArray") {
    assert(iter.func(dict) === Nil)
  }

  test("Does addAll work when input is JsonArray") {
    assert(addAll(arr) === 6)
  }


}
