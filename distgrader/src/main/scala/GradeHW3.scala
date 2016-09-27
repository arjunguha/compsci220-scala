package grading

class HW3Grading(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  import java.nio.file.Paths

    val prefix =
      """
        import Wrangling._
        val gradingBirths = edu.umass.cs.CSV.fromFile("ssa-births.csv")
      """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
      zip.add("""addSbtPlugin("edu.umass.cs" % "compsci220" % "1.0.0")""".getBytes, "project/plugins.sbt")  
      zip.add(Paths.get("cdc-life-expectancy.csv"), "cdc-life-expectancy.csv")
      zip.add(Paths.get("ssa-births.csv"), "ssa-births.csv")
      zip.add("""
           package edu.umass.cs {
             object CSV {
               import java.nio.file._
               import scala.collection.JavaConversions._
               
               def fromFileReal(path: Path): List[List[String]] = {
                 Files.readAllLines(path).map(row => row.split(",").toList).toList
               }

               def fromFile(path: String): List[List[String]] = {
                 if (path == "cdc-life-expectancy.csv" || path == "ssa-births.csv") {
                   fromFileReal(Paths.get(path))
                 }
                 else {
                   List()
                 }
               }
               def fromFile(path: Path): List[List[String]] = List()
               def fromString(csvString: String): List[List[String]] = List()
             }
           }
           """.getBytes,
        "src/main/scala/CSV.scala")
      zip.add(
        s"object GradingMain extends App { $prefix; $body }".getBytes,
        "src/main/scala/GradingMain.scala")
    }

    def body(root: TestCase): Unit = {
      val compiles = root.thenCompile("Check that object Wrangling is defined", "()")

      val yearIs = compiles.thenCompile(
        "Does yearIs have the right type?",
        """def foo(data: List[List[String]], bound: Int): List[List[String]] = yearIs(data, bound)""",
        score = 0)

      yearIs.thenRun(
        "Does yearIs work?",
        """
          val r = yearIs(gradingBirths, 1884)
          assert(r.length == 2)
          assert(r.contains(List("1884", "Unisex A", "F", "20")))
          assert(r.contains(List("1884", "Unisex B", "M", "20")))
        """)

      val yearGT = compiles.thenCompile(
        "Does yearGT have the right type?",
        """def foo(data: List[List[String]], bound: Int): List[List[String]] = yearGT(data, bound)""",
        score = 0)

      yearGT.thenRun(
        "Does yearGT work?",
        """
          val r = yearGT(gradingBirths, 2000)
          assert(r.length == 1)
          assert(r.contains(List("2002", "Mary", "F", "100")))
        """)

      val yearLT = compiles.thenCompile(
        "Does yearLT have the right type?",
        """def foo(data: List[List[String]], bound: Int): List[List[String]] = yearLT(data, bound)""",
        score = 0)

      yearLT.thenRun(
        "Does yearLT work?",
        """
          val r = yearLT(gradingBirths, 1882)
          assert(r.length == 2)
          assert(r.contains(List("1880", "DeadWoman", "F", "900")))
          assert(r.contains(List("1881", "DeadMan", "M", "200")))
        """)

      val onlyName = compiles.thenCompile(
        "Does onlyName have the right type?",
        """def foo(data: List[List[String]], name: String): List[List[String]] = onlyName(data, name)""",
        score = 0)

      onlyName.thenRun(
        "Does onlyName work?",
        """
          val r = onlyName(gradingBirths, "Mary")
          assert(r.contains(List("1883", "Mary", "F", "10000")))
          assert(r.contains(List("2002", "Mary", "F", "100")))
        """)


      val mostPopular = compiles.thenCompile(
        "Does mostPopular have the right type?",
        """def foo(data: List[List[String]]): (String, Int) = mostPopular(data)""",
        score = 0)

      mostPopular.thenRun("Does mostPopular work?",
        """assert(mostPopular(gradingBirths) == ("Mary", 10100))""")

      val count = compiles.thenCompile(
        "Does count have the right type?",
        """def foo(data: List[List[String]]): Int = count(data)""",
        score = 0)

      count.thenRun(
        "Does count work?",
        """assert(count(gradingBirths) == 26600)""")

      val countGirlsAndBoys = compiles.thenCompile(
        "Does countGirlsAndBoys have the right type?",
        """def foo(data: List[List[String]]): (Int, Int) = countGirlsAndBoys(data)""",
        score = 0)

      countGirlsAndBoys.thenRun(
        "Does countGirlsAndBoys work?",
        """assert(countGirlsAndBoys(gradingBirths) == (24140, 2460))""")

      val genderNeutralNames = compiles.thenCompile(
        "Does genderNeutralNames have the right type?",
        """def foo(data: List[List[String]]): Set[String] = genderNeutralNames(data)""",
        score = 0)

      genderNeutralNames.thenRun(
        "Does genderNeutralNames work?",
        """assert(genderNeutralNames(gradingBirths) == Set("Unisex A", "Unisex B"))""")

      val expectedAlive = compiles.thenCompile(
        "Does expectedAlive have the right type?",
        """def foo(gender: String, birthYear: Int, currentYear: Int): Boolean = expectedAlive(gender, birthYear, currentYear)""",
        score = 0)

      expectedAlive.thenRun(
        "Does expectedAlive exclude correctly?",
        """assert(expectedAlive("F", 1930, 2000) == false)""")

      expectedAlive.thenRun(
        "Does expectedAlive include correctly?",
        """assert(expectedAlive("M", 2000, 2050) == true)""")


      val estimatePopulation = compiles.thenCompile(
        "Does estimatePopulation have the right type?",
        """def foo(data: List[List[String]], year: Int): Int = estimatePopulation(data, year)""",
        score = 0)

      estimatePopulation.thenRun(
        "Does estimatePopulation work?",
        """assert(estimatePopulation(yearGT(gradingBirths, 1930), 2050) == 20 + 20 + 20 + 2000 + 100)""")

    }
}
