// You may modify or delete this file
class TestSuite extends org.scalatest.FunSuite {

  import edu.umass.cs.CSV

  test("Have life expectancies from 1930 -- 2010") {
    assert(CSV.fromFile("cdc-life-expectancy.csv").map(x => x(0).toInt).reverse
           == 1930.to(2010))
  }


}