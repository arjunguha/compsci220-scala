import hw.json._
import scala.util.parsing.combinator._

object JsonParser extends JsonParserLike {

  def parse(jsonString: String): Json = ???

}

object JsonPrinter extends JsonPrinterLike {

  def print(json: Json): String = ???

}
