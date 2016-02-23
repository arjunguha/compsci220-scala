import java.util.UUID

class CandyDatabase(candies: Map[String, BigDecimal]) {
  val barcodes : Map[String,UUID] =
    candies.keys.map { c =>
      c -> UUID.randomUUID
    }.toMap
    
  val prices : Map[UUID,BigDecimal] =
    candies.keys.map { c =>
      barcodes(c) -> candies(c)
    }.toMap
  
  def getPriceFromBarcode(b: UUID) : Option[BigDecimal] = {
    if (!prices.contains(b)) {
      None
    } else {
      Some(prices(b))
    }
  }
  
  def getBarcodeForCandy(c: String) : Option[UUID] = {
    if (!barcodes.contains(c)) {
      None
    } else {
      Some(barcodes(c))
    }
  }
}