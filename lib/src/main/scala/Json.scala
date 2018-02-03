package hw.json

import scala.util.parsing.combinator._

/**
 * Companion object to Json. Contains parsing and printing methods.
 */
object Json {
  def print = JPrinter.print _
  def parse = JParser.parse _
  def fromFile = JParser.fromFile _
}

sealed trait Json
case class JsonNull() extends Json
case class JsonNumber(value: Double) extends Json
case class JsonString(value: String) extends Json {
  override def toString: String = "\"" + value + "\""
}
case class JsonBool(value: Boolean) extends Json
case class JsonDict(value: Map[String, Json]) extends Json
case class JsonArray(value: List[Json]) extends Json

trait JsonParserLike extends RegexParsers with PackratParsers {
  def parse(str: String): Json
}

trait JsonPrinterLike {
  def print(e: Json): String
}

private object JPrinter extends JsonPrinterLike {
  def print(json: Json): String = json match {
    case JsonNull() => "null"
    case JsonNumber(n) => n.toString()
    case JsonBool(b) => b.toString()
    case JsonString(s) => "\"" + s + "\""
    case JsonArray(els) => s"[${els.map(print).mkString(", ")}]"
    case JsonDict(map) => {
      val pairs = map.toList.map({ case (k, v) => s""""${k}": ${print(v)}""" })
      "{" + pairs.mkString(", ") + "}"
    }
  }
}

private object JParser extends JsonParserLike {

  import scala.util.parsing.combinator._

  type P[A] = PackratParser[A]

  lazy val string: P[String] = "\"" ~> """([^"]|(?<=\\)")*""".r <~ "\""
  lazy val number: P[Double] = """[-]?\d+(\.\d+)?""".r ^^ (x => x.toDouble)
  lazy val bool: P[Boolean] = (
    "true" ^^ (_ => true) |
    "false" ^^ (_ => false)
  )

  lazy val jsonString: P[Json] = string ^^ (s => JsonString(s))
  lazy val jsonNumber: P[Json] = number ^^ (n => JsonNumber(n))
  lazy val jsonBool: P[Json] = bool ^^ (b => JsonBool(b))
  lazy val jsonNull: P[Json] = "null" ^^ (_ => JsonNull())

  lazy val value: P[Json] =
    jsonString |
    jsonNumber |
    jsonBool   |
    jsonObject |
    jsonArray  |
    jsonNull

  lazy val field: P[(String, Json)] = string ~ ":" ~ value ^^ {
    case s ~ _ ~ v => (s, v)
  }

  lazy val jsonObject: P[Json] = "{" ~> repsep(field, ",") <~ "}" ^^ {
    case fs => JsonDict(fs.toMap)
  }

  lazy val jsonArray: P[Json] = "[" ~> repsep(value, ",") <~ "]" ^^ {
    case els => JsonArray(els)
  }

  def parse(str: String): Json = parseAll(value, str.trim) match {
    case Success(s, _) => s
    case m => throw new Exception(m.toString)
  }

  def fromFile(path: String): List[Json] = {
    import java.nio.file._
    parseAll(rep(value), (new String(Files.readAllBytes(Paths.get(path))))) match {
      case Success(s, _) => s
      case m => throw new Exception(m.toString)
    }
  }
}

