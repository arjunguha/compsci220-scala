package hw.json
import scala.util.parsing.combinator._

/**
 * Object that contains helper function for printing and parsing Json
 * values.
 */
object JsonHelper {

  /**
   * Parse a string containing valid Json.
   *
   * @param str The string representing the Json value to be parsed.
   * @return A [[Json]] value.
   */
  def parse(str: String) = JParser.parse(str)

  /**
   * Parse a file containing Json objects.
   *
   * @param path The path to the file relative to the project root.
   * @return A list of [[Json]] objects
   */
  def fromFile(path: String): List[Json] = JParser.fromFile(path)
}

/**
 * Trait that encodes Json values in Scala.
 */
sealed trait Json

/**
 * Case class to represent the `null` value in Json.
 */
case class JsonNull() extends Json
/**
 * Case class to reprent numbers in Json.
 */
case class JsonNumber(value: Double) extends Json
/**
 * Case class to reprent strings in Json.
 */
case class JsonString(value: String) extends Json

/**
 * Case class to reprent booleans (`true` and `false`) in Json.
 */
case class JsonBool(value: Boolean) extends Json
/**
 * Case class to represent a Dictionary in Json.
 */
case class JsonDict(value: Map[JsonString, Json]) extends Json
/**
 * Case class to represent an Array in Json.
 */
case class JsonArray(value: List[Json]) extends Json

private object JParser extends RegexParsers with PackratParsers {

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

  lazy val field: P[(JsonString, Json)] = string ~ ":" ~ value ^^ {
    case s ~ _ ~ v => (JsonString(s), v)
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

