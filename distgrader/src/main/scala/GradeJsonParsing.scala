package grading

class GradeJsonParsing(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix =
    """
    import hw.json.Json
    import JsonParser.parse
    import JsonPrinter.print

    def testParse(str: String, json: Json): Unit = {
      assert(parse(str) === json)
    }

    def testPrint(json: Json): Unit = {
      val r = parse(print(json))
      assert(r === json)
    }

    """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add("""addSbtPlugin("edu.umass.cs" % "compsci220" % "1.0.2")""".getBytes, "project/plugins.sbt")
    zip.add(s"object GradingMain extends App { $prefix $body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Does the program compile?", "()")

    /* Parsing tests */
    compiles.thenRun("parse num", """testParse("1", JsonNumber(1))""" )
    compiles.thenRun("parse double", """testParse("1.1", JsonNumber(1.1))""" )
    compiles.thenRun("parse string",
      """testParse("\"string\"", JsonString("string"))""" )
    compiles.thenRun("parse true", """testParse("true", JsonBool(true))""" )
    compiles.thenRun("parse false", """testParse("false", JsonBool(false))""" )
    compiles.thenRun("parse null", """testParse("null", JsonNull())""" )

    val obj = """ {"a": 1, "b": 2} """
    val arr = """ [1, 2, 3, 4] """

    compiles.thenRun("parse object",
      s"""testParse($obj, JsonDict(Map("a" -> 1, "b" -> 2)))""" )

    compiles.thenRun("parse array",
      s"""testParse($arr, JsonArray(List(1, 2, 3, 4)))""" )

    compiles.thenRun("parse array in obj",
      s""" testParse({"arr": $arr}, JsonDict(Map("arr" -> JsonArray(List(1, 2, 3, 4)) )) """ )

    /* Printing tests */
    compiles.thenRun("print num", """testPrint( JsonNumber(1))""" )
    compiles.thenRun("print double", """testPrint( JsonNumber(1.1))""" )
    compiles.thenRun("print string",
      """testPrint( JsonString("string"))""" )
    compiles.thenRun("print true", """testPrint( JsonBool(true))""" )
    compiles.thenRun("print false", """testPrint( JsonBool(false))""" )
    compiles.thenRun("print null", """testPrint( JsonNull())""" )

    val obj = """ {"a": 1, "b": 2} """
    val arr = """ [1, 2, 3, 4] """

    compiles.thenRun("print object",
      s"""testPrint( JsonDict(Map("a" -> 1, "b" -> 2)))""" )

    compiles.thenRun("print array",
      s"""testPrint( JsonArray(List(1, 2, 3, 4)))""" )

    compiles.thenRun("print array in obj",
      s""" testPrint( JsonDict(Map("arr" -> JsonArray(List(1, 2, 3, 4)) )) """ )
  }
}
