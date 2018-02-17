import Wrangling._
import hw.json._

class GradingTests extends org.scalatest.FunSuite {

  val data = JsonHelper.fromFile("dataset.json")

  def getName(json: Json): String = json match {
    case JsonDict(map) => map(JsonString("name")) match {
      case JsonString(str) => str
      case _ => throw new Exception("Invalid Json format")
    }
      case _ => throw new Exception("Invalid Json format")
  }

  def names(jl: List[Json]): Set[String] = jl.map(getName).toSet

  test("Does key work when object is a JsonDict and has key?") {
    val json = JsonDict(Map(JsonString("a") -> JsonNumber(1)))
    assert(key(json,"a") == Some(JsonNumber(1)))
  }

  test("Does key produce Node when object is a JsonDict and does not have the key?") {
    val json = JsonDict(Map(JsonString("b") -> JsonNumber(1)))
    assert(key(json, "a") == None)
  }

  test("Does key produce None when object is not a JsonDict?") {
    val json = JsonString("a")
    assert(key(json, "a") == None)
  }

  test("Does fromState work?") {
    val r = fromState(data, "CA")
    assert(r.length == 2)
    assert(names(r) == Set("food1", "food3"))
  }

  test("Does ratingGT work?") {
    val r = ratingGT(data, 3.5)
    assert(r.length == 2)
    assert(names(r) == Set("place1", "food3"))
  }

  test("Does ratingLT work?") {
    val r = ratingLT(data, 3)
    assert(r.length == 2)
    assert(names(r) == Set("food1", "food2"))
  }

  test("Does category work?") {
    val r = category(data, "Food")
    assert(r.length == 3)
    assert(names(r) == Set("food1", "food2", "food3"))
  }

  test("Does groupByState work?") {
    val r = groupByState(data)
    assert(r("MA").length == 2)
    assert(r("CA").length == 2)
    assert(names(r("CA")) == Set("food1", "food3"))
    assert(names(r("MA")) == Set("place1", "food2"))
  }

  test("Does groupByCategory work?") {
    val r = groupByCategory(data)
    assert(r("Food").length == 3)
    assert(r("Place").length == 1)
    assert(names(r("Place")) == Set("place1"))
    assert(names(r("Food")) == Set("food1", "food2", "food3"))
  }

  test("Does bestPlace work?") {
    val r = bestPlace(data).get
    assert(getName(r) == "food3")
  }

  test("Does hasAmbience work?") {
    val r = hasAmbience(data, "casual")
    assert(names(r) == Set("food1"))
  }

}
