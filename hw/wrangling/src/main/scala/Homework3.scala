import com.github.tototoshi.csv._

object Homework3 {


  val reader = CSVReader.open(new java.io.File("ssa-baby-names.csv"))
  val births = reader.all
  reader.close

  val r = CSVReader.open(new java.io.File("cdc-life-expectancy.csv"))
  val lifeExpectancy = r.all
  reader.close

  def yearGT(data: List[List[String]], bound: Int): List[List[String]] = {
    data.filter(_(0).toInt > bound)
  }

  def yearLT(data: List[List[String]], bound: Int): List[List[String]] = {
    data.filter(_(0).toInt < bound)
  }

  def onlyName(data: List[List[String]], name: String): List[List[String]] = {
    data.filter(_(1) == name)
  }

  def mostPopular(data: List[List[String]]): (String, Int) = {
    data.maxBy(_(3).toInt) match {
      case List(_, name, _, year) => (name, year.toInt)
      case _ => ???
    }
  }

  def countGirlsAndBoys(data: List[List[String]]): (Int, Int) = {
    val (girls, boys) = data.partition(_(2) == "F")
    (girls.map(_(3).toInt).sum, boys.map(_(3).toInt).sum)
  }

  def unisexNames(data: List[List[String]]): Set[String] = {
    val (girls, boys) = data.partition(_(2) == "F")
    val girlNames = girls.map(_(1)).toSet
    val boyNames = boys.map(_(1)).toSet
    girlNames intersect boyNames
  }

  def expectedAlive(gender: String, birthYear: Int, currentYear: Int): Boolean = {
    if (birthYear > currentYear) {
      false
    }
    else {
      lifeExpectancy.find(_(0).toInt == birthYear) match {
        case None => true
        case Some(lst) => currentYear <= (if (gender == "F") lst(2) else lst(1)).toInt + birthYear
      }
    }
  }

  def estimatePopulation(data: List[List[String]], year: Int): Int = {
    data.map(x => if (expectedAlive(x(2), x(0).toInt, year)) x(3).toInt else 0).sum
  }

  // def ageDistribution(data: List[List[String]], year: Int): Map[Int, Int] = {
  //   data.map(x => if

}