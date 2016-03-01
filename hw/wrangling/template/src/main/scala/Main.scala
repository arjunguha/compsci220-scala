object Homework3 {

  import edu.umass.cs.CSV


  // WARNING: this may take a very long time. Cut the file or work with a
  // small, made-up dataset if you have trouble.
  // val allBirths = CSV.fromFile("ssa-births.csv")

  val lifeExpectancy = CSV.fromFile("cdc-life-expectancy.csv")

  /** Restrict the data to the year `year`. */
  def yearIs(data: List[List[String]], n: Int): List[List[String]] = ???


  /** Restrict the data to years greater than `bound`. */
  def yearGT(data: List[List[String]], bound: Int): List[List[String]] = ???

  /** Restrict the data to years less than `bound` */
  def yearLT(data: List[List[String]], bound: Int): List[List[String]] = ???

  /** Restrict the data to the name `name`. */
  def onlyName(data: List[List[String]], name: String): List[List[String]] = ???

  /** Calculate the most popular name and the *total* number of children born
      with that name.

      Hint: It is likely that children are born with the same
      name in several years. So, you should first calculate the total
      number of childen with each name.
   */
  def mostPopular(data: List[List[String]]): (String, Int) = ???

  /** Calculate the number of children born in the given dataset. */
  def count(data: List[List[String]]): Int = ???


  /** Produce a tuple with the number of girls and boys respectively. */
  def countGirlsAndBoys(data: List[List[String]]): (Int, Int) = ???

  /** Calculate the set of names that are given to both girls and boys. */
  def unisexNames(data: List[List[String]]): Set[String] = ???


  /** Determine if a person with the specified `gender` (either "M" or "F") who
      was born in `birthYear` is expected to be alive, according to the CDC
      life-expectancy data.

      If `currentYear` is the last year the person is estimated to be alive, be
      optimistic and produce `true`.

      The CDC data only ranges from 1930 -- 2010. Therefore, assume that
      `birthYear` is in this range too. */
  def expectedAlive(gender: String, birthYear: Int, currentYear: Int): Boolean = ???

  /** Estimate how many people from `data` will be alive in `year`. */
  def estimatePopulation(data: List[List[String]], year: Int): Int = ???

}