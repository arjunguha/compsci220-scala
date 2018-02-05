import hw.json._
import hw.combinator._

object Combinators extends CombinatorLike {

  def key(k: String) = new JsonProc[Json,Json](json => json match {
    case JsonDict(kv) => List(kv.get(JsonString(k))).flatten
    case v => Nil
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

  def addAll(json: Json): Double =
    (iter >>> number).func(json).foldRight(0.0)((acc: Double, x: Double) => x + acc)

  def recur = new JsonProc[Json, Json](json => json match {
    case JsonNull() => List(json)
    case JsonNumber(_) => List(json)
    case JsonString(_) => List(json)
    case JsonBool(_) => List(json)
    case JsonDict(map) => map.toList.flatMap({ case (_, v) => recur.func(v) })
    case JsonArray(vs) => vs.flatMap(v => recur.func(v))
  })

  def split[S] = new JsonProc[S, (S, S)](json => List((json, json)))

  def first[A, B, C](proc: JsonProc[A, B]) = new JsonProc[(A, C), (B, C)](json =>
      json match {
        case (a, b) => proc.func(a).map(a1 => (a1, b))
      })

  def swap[A, B] = new JsonProc[(A, B), (B, A)](tup => List((tup._2, tup._1)))

  def second[A, B, C](proc: JsonProc[A, B]): JsonProc[(C, A), (C, B)] = {
    // Explicit type annotations are required.
    swap[C, A] >>> first(proc) >>> swap[B, C]
  }

  def combine[A] = new JsonProc[(A, A), A](tup => List(tup._1, tup._2))

}
