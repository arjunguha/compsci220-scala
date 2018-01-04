import hw.json._
import scala.util.parsing.combinator._

object JSONParser extends JSONParserLike {

  def parseJSON(jsonString: String): JSONLike = ???

}

object JSONPrinter extends JSONPrinterLike {

  def printJSON(json: JSONLike): String = ???

}
