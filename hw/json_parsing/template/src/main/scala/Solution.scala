import hw.json._
import scala.util.parsing.combinator._

object JSON {
  // Define the structure of JSON as scala case classes that extend JSONLike.
}

object JSONParser extends JSONParserLike {

  def parseJSON(jsonString: String): JSONLike = ???

}

object JSONPrinter extends JSONPrinterLike {

  def printJSON(json: JSONLike): String = ???

}

object JSONManipulate extends JSONManipulateLike {

  def getAgeArrays(jsonString: String): String = ???

  def getTreeNodes(jsonString: String, nodeName: String): String = ???

}
