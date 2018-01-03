import scala.util.parsing.combinator._

import JSON._

object JParser extends RegexParsers with PackratParsers {

  type P[A] = PackratParser[A]

  lazy val string: P[String] = "\"" ~> "[^\"]*".r <~ "\""

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

  def parse(str: String): Json = parseAll(value, str) match {
    case Success(s, _) => s
    case m => throw new Exception(m.toString)
  }
}
