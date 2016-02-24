import org.scalatest.FunSuite

class ExerciseTests extends FunSuite {
  val candies : Map[String,BigDecimal] = Map(
    "Snozzberries" -> BigDecimal(2.49),
    "Everlasting Gobstopper" -> BigDecimal(0.99),
    "Fizzy Lifting Drink" -> BigDecimal(1.99),
    "Edible Teacup" -> BigDecimal(4.79),
    "Wonka Bar" -> BigDecimal(1.50)
  )
  
  def barcodeTester(database: CandyDatabase, candy_name: String) : BigDecimal = {
    database
    // query the database for the barcode
      .getBarcodeForCandy(candy_name)
    // and then query the database for the price using the barcode
      .flatMap(database.getPriceFromBarcode)
    // return $0 if any of those lookups fail
      .getOrElse(BigDecimal(0))
  }
  
  val db = new CandyDatabase(candies)
  
  test("Everlasting Gobstoppers cost $0.99.") {
    val price = barcodeTester(db, "Everlasting Gobstopper")
    
    assert(price == candies("Everlasting Gobstopper"))
  }
  
  test("There is no price on file for a River of Chocolate.") {
    val price = barcodeTester(db, "River of Chocolate")
    
    assert(price == BigDecimal(0))
  }
}