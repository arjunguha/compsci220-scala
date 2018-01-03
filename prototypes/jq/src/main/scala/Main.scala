object JSON {
  sealed trait Json
  // "case object" would introduce curricular dependency
  case class JsonNull() extends Json
  case class JsonNumber(value: Double) extends Json
  case class JsonString(value: String) extends Json
  case class JsonBool(value: Boolean) extends Json
  // need to teach collections to do JSON
  case class JsonDict(value: Map[String, Json]) extends Json
  case class JsonArray(value: List[Json]) extends Json
}

object Jq {

  import JSON._

  // The generics makes it possible to define extractors that produce primitive
  // Scala values. It makes this harder to understand. Perhaps the assignment
  // could give students a monomorphic version of JsonProc[Json, Json] and then
  // have them refactor it to a generic version.
  class JsonProc[S,T](val func: S => List[T]) {

    def >>>[U](other: JsonProc[T, U]) =
      new JsonProc[S, U](json => func(json).flatMap(other.func))

    // TODO(rachit): Student solution will NOT contain the apply method. Only
    // here for prototyping.
    def apply(v: S): List[T] = func(v)
  }

  def key(k: String) = new JsonProc[Json,Json](json => json match {
    case JsonDict(kv) => List(kv.getOrElse(k, JsonNull()))
    case v => Nil // The jq command-line tool throws an exception in these cases.
  })

  def index(n: Int) = new JsonProc[Json,Json](json => json match {
    case JsonArray(arr) => List(if (n < arr.length) arr(n) else JsonNull())
    case v => Nil
  })

  def iter = new JsonProc[Json,Json](json => json match {
    case JsonArray(arr) => arr
    case v => Nil
  })

  def number = new JsonProc[Json,Double](json => json match {
    case JsonNumber(n) => List(n)
    case _ => Nil
  })

  //def comma[S, T](filter1: JsonProc[S, T], filter2: JsonProc[S, T]) =
    //new JsonProc[S, T](json => filter1(json) ++ filter2(json))

  // Get all leaf values
  def leaves(json: Json): List[Json] = json match {
    case JsonNull() => List(json)
    case JsonNumber(_) => List(json)
    case JsonString(_) => List(json)
    case JsonBool(_) => List(json)
    case JsonDict(map) => map.toList.flatMap({ case (_, v) => recur(v) })
    case JsonArray(vs) => vs.flatMap(v => recur(v))
  }

  def recur = new JsonProc[Json, Json](json => leaves(json))

  def split[S] = new JsonProc[S, (S, S)](json => List((json, json)))

  def first[A, B, C](proc: JsonProc[A, B]) = new JsonProc[(A, C), (B, C)](json =>
      json match {
        case (a, b) => proc(a).map(a1 => (a1, b))
      })

  def swap[A, B] = new JsonProc[(A, B), (B, A)](tup => List((tup._2, tup._1)))

  def second[A, B, C](proc: JsonProc[A, B]): JsonProc[(C, A), (C, B)] = {
    // Explicit type annotations are required.
    swap[C, A] >>> first(proc) >>> swap[B, C]
  }

  def combine[A] = new JsonProc[(A, A), A](tup => List(tup._1, tup._2))

}

object Examples {

  import Jq._
  import JSON._
  import JParser.parse

  def ages = iter >>> key("x") >>> number

  def both = comma(key("x"), key("y") >>> key("a")) >>> number

  def both2 =
    split[Json] >>> first(key("x")) >>> second(key("y") >>> key("a")) >>> combine >>> number

  val json = parse("""
    { "x": 200, "y": {"a": 100} }
    """
  )

  //val json2 = parse("""
    //[{ "x": 1 }, { "y" : 2 }, 3, 4, true, "b"]
    //"""
  //)

  //def rec = recur >>> number

  def main(args: Array[String]) {
    println(both2(json))
    //println(rec(json2))
  }

}

