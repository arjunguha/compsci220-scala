import hw.csv._
import Main._

// NOTE: The test cases for sort and merge support lessThan and !lessThan
class GradingTests extends org.scalatest.FunSuite {

  val gradingBirthsString = """1880,DeadWoman,F,900
1881,DeadMan,M,200
1883,Mary,F,10000
1883,Jill,F,10000
1884,Unisex A,F,20
1884,Unisex B,M,20
1918,WW2VetGirl,F,100
1920,WW2VetGuy,M,200
1949,BoomerBoy,M,2000
1950,BoomerGirl,F,1000
1984,Unisex B,F,20
1984,Unisex A,M,20
2000,MillenialBoy,M,20
1999,MillenialGirl,F,2000
2002,Mary,F,100"""

  val cdcString = """2010,76,81
2009,75,80
2008,75,80
2007,75,80
2006,75,80
2005,75,80
2004,75,80
2003,75,80
2002,75,80
2001,74,80
2000,74,80
1999,74,79
1998,74,80
1997,74,79
1996,73,79
1995,73,79
1994,72,79
1993,72,79
1992,72,79
1991,72,79
1990,72,79
1989,72,79
1988,71,78
1987,71,78
1986,71,78
1985,71,78
1984,71,78
1983,71,78
1982,71,78
1981,70,78
1980,70,77
1979,70,78
1978,70,77
1977,70,77
1976,69,77
1975,69,77
1974,68,76
1973,68,75
1972,67,75
1971,67,75
1970,67,75
1969,67,73
1968,67,73
1967,67,73
1966,67,73
1965,67,73
1964,67,73
1963,67,73
1962,67,73
1961,67,73
1960,67,73
1959,66,71
1958,66,71
1957,66,71
1956,66,71
1955,66,71
1954,66,71
1953,66,71
1952,66,71
1951,66,71
1950,66,71
1949,61,65
1948,61,65
1947,61,65
1946,61,65
1945,61,65
1944,61,65
1943,61,65
1942,61,65
1941,61,65
1940,61,65
1939,60,64
1938,60,64
1937,60,64
1936,60,64
1935,60,64
1934,58,62
1933,58,62
1932,58,62
1931,58,62
1930,58,62"""

  def csvFromString(csvString: String): List[List[String]] = {
    val reader = com.github.tototoshi.csv.CSVReader.open(
      new java.io.StringReader(csvString))
    val result = reader.all()
    reader.close()
    result
  }

  def myReadSSARow(row: List[String]): SSARow =
    SSARow(row(0).toInt, row(1), if (row(2) == "M") Male() else Female(), row(3).toInt)

  def myReadCDCRow(row: List[String]): CDCRow =
    CDCRow(row(0).toInt, row(1).toInt, row(2).toInt)

  val gradingBirths = csvFromString(gradingBirthsString).map(myReadSSARow)

  val lifeExpectancies = csvFromString(cdcString).map(myReadCDCRow)

  test("Does yearIs work?") {
    val r = yearIs(gradingBirths, 1884)
    assert(r.length == 2)
    assert(r.contains(SSARow(1884, "Unisex A", Female(), 20)))
    assert(r.contains(SSARow(1884, "Unisex B", Male(), 20)))
  }

  test("Does yearGT work?") {
    val r = yearGT(gradingBirths, 2000)
    assert(r.length == 1)
    assert(r.contains(SSARow(2002, "Mary", Female(), 100)))
  }

  test("Does yearLT work?") {
    val r = yearLT(gradingBirths, 1882)
    assert(r.length == 2)
    assert(r.contains(SSARow(1880, "DeadWoman", Female(), 900)))
    assert(r.contains(SSARow(1881, "DeadMan", Male(), 200)))
  }


  test("Does onlyName work?") {
    val r = onlyName(gradingBirths, "Mary")
    assert(r.contains(SSARow(1883, "Mary", Female(), 10000)))
    assert(r.contains(SSARow(2002, "Mary", Female(), 100)))
  }

  test("Does mostPopular work?") {
    assert(mostPopular(gradingBirths) == ("Mary", 10100))
  }

  test("Does count work?") {
    assert(count(gradingBirths) == 26600)
  }

  test("Does countGirlsAndBoys work?") {
    assert(countGirlsAndBoys(gradingBirths) == (24140, 2460))
  }

  test("Does genderNeutralNames work?") {
    assert(genderNeutralNames(gradingBirths) == Set("Unisex A", "Unisex B"))
  }


  test("Does expectedAlive exclude correctly?") {
    assert(expectedAlive(Female(), 1930, 2000, lifeExpectancies) == false)
  }

  test("Does expectedAlive include correctly?") {
    assert(expectedAlive(Male(), 2000, 2050, lifeExpectancies) == true)
  }


  test("Does estimatePopulation work?") {
    assert(
      estimatePopulation(yearGT(gradingBirths, 1930), 2050, lifeExpectancies) == 
      20 + 20 + 20 + 2000 + 100)
  }

}
