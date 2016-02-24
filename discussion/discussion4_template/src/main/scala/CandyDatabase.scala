import java.util.UUID

class CandyDatabase(candies: Map[String, BigDecimal]) {
  val barcodes : Map[String,UUID] = ???
    
  val prices : Map[UUID,BigDecimal] = ???
  
  def getPriceFromBarcode(b: UUID) : Option[BigDecimal] = ???
  
  def getBarcodeForCandy(c: String) : Option[UUID] = ???
}