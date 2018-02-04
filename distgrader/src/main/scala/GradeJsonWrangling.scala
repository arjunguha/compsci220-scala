package grading

class JsonWranglingGrading(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  import java.nio.file.Paths

    val prefix =
      """
        import Wrangling._
        import hw.json._
        val gradingBirths = edu.umass.cs.CSV.fromFile("ssa-births.csv")

        def getName(json: Json): String = json match {
          case JsonDict(map) => map("name") match {
            case JsonString(str) => str
            case _ => throw new Exception("Invalid Json format")
          }
          case _ => throw new Exception("Invalid Json format")
        }

        def names(jl: List[Json]): Set[Json] = jl.map(getName).toSet

        def getUid(json: Json): Double = json match {
          case JsonDict(map) => map("name") match {
            case JsonNumber(n) => n
            case _ => throw new Exception("Invalid Json format")
          }
          case _ => throw new Exception("Invalid Json format")
        }
      """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
      zip.add(
        """addSbtPlugin("edu.umass.cs" % "compsci220" % "1.0.0")""".getBytes,
        "project/plugins.sbt")

      zip.add(Paths.get("dataset.json"), "dataset.json")
      zip.add(
        s"object GradingMain extends App { $prefix; $body }".getBytes,
        "src/main/scala/GradingMain.scala")
    }

    def body(root: TestCase): Unit = {
      val compiles = root.thenCompile("Check that object Wrangling is defined", "()")

      val key = compiles.thenCompile(
        "Does key have the right type?",
        """def f(j: Json, s: String): Option[Json] = key(j, s)""",
        score=0)

      key.thenRun(
        "Does key work when object is a JsonDict and has key?",
        """
        val json = JsonDict(Map(JsonString("a") -> 1))
        assert(key(json,"a") == Some(1))
        """,
        score=0)

      key.thenRun(
        "Does key work when object is a JsonDict and does not have the key?",
        """
        val json = JsonDict(Map(JsonString("b") -> 1))
        assert(key(json, "a") == Some(1))
        """,
        score=0)

      key.thenRun(
        "Does key work when object is not a JsonDict?",
        """
        val json = JsonString("a")
        assert(key(json, "a") == None)
        """,
        score=0)

      val fromState = compiles.thenCompile(
        "Does fromState have the right type?",
        """def f(d: List[Json], s: String): List[Json] = fromState(d, s)""",
        score = 0)

      fromState.thenRun(
        "Does fromState work?",
        """
          val r = fromState(data, "CA")
          assert(r.length == 2)
          assert(names(r) == Set("food1", "food2"))
        """
      )

      val ratingGT = compiles.thenCompile(
        "Does ratingGT have the right type?",
        """def f(d: List[Json], s: String): List[Json] = ratingGT(d, s)""",
        score = 0)

      ratingGT.thenRun(
        "Does ratingGT work?",
        """
          val r = ratingGT(data, 3.5)
          assert(r.length == 2)
          assert(names(r) == Set("place1", "food3"))
        """
      )

      val ratingLT = compiles.thenCompile(
        "Does ratingLT have the right type?",
        """def f(d: List[Json], s: String): List[Json] = ratingLT(d, s)""",
        score = 0)

      ratingLT.thenRun(
        "Does ratingLT work?",
        """
          val r = ratingLT(data, 3)
          assert(r.length == 2)
          assert(names(r) == Set("food1", "food2"))
        """
      )

      val category = compiles.thenCompile(
        "Does category have the right type?",
        """def f(d: List[Json], s: String): List[Json] = category(d, s)""",
        score = 0)

      category.thenRun(
        "Does category work?",
        """
          val r = category(data, "Food")
          assert(r.length == 3)
          assert(names(r) == Set("food1", "food2", "food3")
        """
      )

      val groupByState = compiles.thenCompile(
        "Does groupByState have the right type?",
        """def f(d: List[Json]): Map[String, List[Json]] = groupByState(d)""",
        score = 0)

      groupByState.thenRun(
        "Does groupByState work?",
        """
          val r = groupByState(data)
          assert(r("MA").length == 2)
          assert(r("CA").length == 2)
          assert(names(r("CA")) == Set("food1", "food3"))
          assert(names(r("MA")) == Set("place1", "food2"))
        """
      )

      val groupByCategory = compiles.thenCompile(
        "Does groupByCategory have the right type?",
        """def f(d: List[Json]): Map[String, List[Json]] = groupByCategory(d)""",
        score = 0)

      groupByCategory.thenRun(
        "Does groupByCategory work?",
        """
          val r = groupByCategory(data)
          assert(r("Food").length == 3)
          assert(r("Place").length == 1)
          assert(names(r("Place")) == Set("place1"))
          assert(names(r("Food")) == Set("food1", "food2", "food3"))
        """
      )

      val bestPlace = compiles.thenCompile(
        "Does bestPlace have the right type?",
        """def f(d: List[Json]): Map[String, List[Json]] = bestPlace(d)""",
        score = 0)

      bestPlace.thenRun(
        "Does bestPlace work?",
        """
          val r = bestPlace(data)
          assert(getName(r) == "food3")
        """
      )

      val hasAmbience = compiles.thenCompile(
        "Does hasAmbience have the right type?",
        """def f(d: List[Json], a: String): List[Json] = hasAmbience(d, a)""",
        score = 0)

      hasAmbience.thenRun(
        "Does hasAmbience work?",
        """
          val r = hasAmbience(data, "casual")
          assert(names(r) == "food1")
        """
      )
    }
}
