import scala.util.parsing.combinator._

sealed trait Json
case class JsonNull() extends Json
case class JsonNumber(value: Double) extends Json
case class JsonString(value: String) extends Json
case class JsonBool(value: Boolean) extends Json
case class JsonDict(value: Map[String, Json]) extends Json
case class JsonArray(value: List[Json]) extends Json

trait JsonParserLike extends RegexParsers with PackratParsers {
  def parse(str: String): Json
}

trait JsonPrinterLike {
  def print(e: Json): String
}
