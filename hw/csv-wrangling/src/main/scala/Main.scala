import  hw.csv._

sealed trait Gender
case class Male() extends Gender
case class Female() extends Gender

case class SSARow(birthYear: Int, name: String, gender: Gender, count: Int)

case class CDCRow(birthYear: Int, maleLifeExpectancy: Int,
  femaleLifeExpectancy: Int)

object Main {

  def readSSARow(row: List[String]): SSARow =
    SSARow(row(0).toInt, row(1), if (row(2) == "M") Male() else Female(), row(3).toInt)

  def readCDCRow(row: List[String]): CDCRow =
    CDCRow(row(0).toInt, row(1).toInt, row(2).toInt)

  def yearIs(rows: List[SSARow], bound: Int): List[SSARow] =
    rows.filter(row => row.birthYear == bound)

  def yearGT(rows: List[SSARow], bound: Int): List[SSARow] =
    rows.filter(row => row.birthYear > bound)

  def yearLT(rows: List[SSARow], bound: Int): List[SSARow] =
    rows.filter(row => row.birthYear < bound)

  def onlyName(rows: List[SSARow], name: String): List[SSARow] =
    rows.filter(row => row.name == name)

  def mostPopular(rows: List[SSARow]): (String, Int) =
    rows.groupBy(row => row.name)
        .mapValues(alist => alist.map(row => row.count).sum)
        .toList
        .sortBy(tuple => tuple._2)
        .last

  def count(rows: List[SSARow]): Int =
    rows.map(row => row.count).sum

  def countGirlsAndBoys(rows: List[SSARow]): (Int, Int) =
    (count(rows.filter(row => row.gender == Female())),
     count(rows.filter(row => row.gender == Male())))

  def genderNeutralNames(rows: List[SSARow]): Set[String] = {
    val girlNames = rows.filter(row => row.gender == Female()).map(row => row.name).toSet
    val boyNames = rows.filter(row => row.gender == Male()).map(row => row.name).toSet
    boyNames.intersect(girlNames)
  }

  def expectedAlive(gender: Gender, birthYear: Int, currentYear: Int,
    lifeExpectancies: List[CDCRow]): Boolean = {
      if (birthYear > currentYear) {
        false
      }
      else {
        lifeExpectancies.find(row => row.birthYear == birthYear) match {
          case None => true
          case Some(le) => currentYear <= birthYear + (if (gender == Female()) le.femaleLifeExpectancy else le.maleLifeExpectancy)
        }
      }
  }

  def estimatePopulation(rows: List[SSARow], year: Int,
    lifeExpectancies: List[CDCRow]): Int =
    count(rows.filter(x => expectedAlive(x.gender, x.birthYear, year, lifeExpectancies)))

}