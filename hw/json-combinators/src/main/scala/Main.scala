import hw.json._
import hw.combinator._

object Combinators extends CombinatorLike {

  def key(k: String): JsonProc[Json, Json] =  JsonProc(json => json match {
    case JsonDict(kv) => List(kv.get(JsonString(k))).flatten
    case v => Nil
  })

  def index(n: Int): JsonProc[Json, Json] =  JsonProc(json => json match {
    case JsonArray(arr) if (n < arr.length) => List(arr(n))
    case JsonArray(arr) if (n >= arr.length) => Nil
    case v => Nil
  })

  def iter: JsonProc[Json, Json] =  JsonProc(json => json match {
    case JsonArray(arr) => arr
    case v => Nil
  })

  def number: JsonProc[Json, Double] =  JsonProc(json => json match {
    case JsonNumber(n) => List(n)
    case _ => Nil
  })

  def string: JsonProc[Json, String] =  JsonProc(json => json match {
    case JsonString(s) => List(s)
    case _ => Nil
  })

  def recur: JsonProc[Json, Json] =  JsonProc(json => json match {
    case JsonNull() => List(json)
    case JsonNumber(_) => List(json)
    case JsonString(_) => List(json)
    case JsonBool(_) => List(json)
    case JsonDict(map) => map.toList.flatMap({ case (_, v) => recur.func(v) })
    case JsonArray(vs) => vs.flatMap(v => recur.func(v))
  })

  def addAll(json: Json): Double =
    (iter >>> number).func(json).foldRight(0.0)((acc: Double, x: Double) => x + acc)

  def allNumbers(json: Json): List[Double] =
    (recur >>> number).func(json)

  def split[S]: JsonProc[S, (S, S)] =  JsonProc(json => List((json, json)))

  def first[A, B, C](proc: JsonProc[A, B]): JsonProc[(A, C), (B, C)] =
     JsonProc(json =>
        json match {
          case (a, b) => proc.func(a).map(a1 => (a1, b))
        })

  def swap[A, B]: JsonProc[(A, B), (B, A)] =
     JsonProc(tup => List((tup._2, tup._1)))

  def second[A, B, C](proc: JsonProc[A, B]): JsonProc[(C, A), (C, B)] = {
    // Explicit type annotations are required.
    swap[C, A] >>> first(proc) >>> swap
  }

  def combine[A]: JsonProc[(A, A), A] =  JsonProc(tup => List(tup._1, tup._2))

  def extractNameAndAge(json: Json): Option[(Json, Json)] = {
    val name = key("name")
    val age = key("age")
    val extract: JsonProc[Json, (Json, Json)] =
      split[Json] >>> first(name) >>> second(age)
    extract.func(json).headOption
  }

  def calculateAge(json: Json): Option[Double] = {
    val extract: JsonProc[Json, (Double, Double)] =
      split[Json] >>> first(key("born") >>> number) >>> second(key("died") >>> number)

    // The inner function can also be a JsonProc
    extract.func(json).headOption.map({ case (b, d) => d - b })
  }

}
