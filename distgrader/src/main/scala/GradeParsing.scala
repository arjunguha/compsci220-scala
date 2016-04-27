package grading

class GradeParsing(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix =
    """
    import hw.parsing._
    import ArithParser.parseArith
    import ArithEval.eval
    import ArithPrinter.print

    def testPP(exp: Expr): Unit = {
      val r = parseArith(print(exp))
      assert(r == exp ||  eval(r) == eval(exp))
    }

    def testP(str: String, exp: Expr, result: Double): Unit = {
      val r = parseArith(str)
      assert(r == exp || eval(r) == result)
      }
    """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add("""
        resolvers += "PLASMA" at "https://dl.bintray.com/plasma-umass/maven"
        libraryDependencies += "edu.umass.cs" %% "compsci220" % "1.2.1"
            """.getBytes,
      "build.sbt")
    zip.add(s"object GradingMain extends App { $prefix $body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Does the program compile?", "()")



    compiles.thenRun("1000", """testP("1000", Num(1000), 1000)""")
    compiles.thenRun("2 + 3", """testP("2 + 3", Add(Num(2), Num(3)), 5)""")
    compiles.thenRun("10 * 5", """testP("10 * 5", Mul(Num(10), Num(5)), 50)""")
    compiles.thenRun("2 * 3 + 4", """testP("2 * 3 + 4", Add(Mul(Num(2), Num(3)), Num(4)), 10)""")

    compiles.thenRun("2 + 3 * 4", """testP("2 + 3 * 4", Add(Num(2), Mul(Num(3), Num(4))), 14)""")
    compiles.thenRun("(2 + 3) * 4", """testP("(2 + 3) * 4", Mul(Add(Num(2), Num(3)), Num(4)), 20)""")
    compiles.thenRun("2 + (3 * 4)", """testP("2 + (3 * 4)", Add(Num(2), Mul(Num(3), Num(4))), 14)""")

    compiles.thenRun("print.parse sum", """testPP(Add(Num(2), Num(3)))""")
    compiles.thenRun("print.parse prod", """testPP(Mul(Num(2), Num(3)))""")
    compiles.thenRun("print.parse precedence", """testPP(Add(Mul(Num(2), Num(3)), Num(4)))""")

    compiles.thenRun("print.parse precedence (parens required)", """testPP(Mul(Add(Num(2), Num(3)), Num(4)))""")


    compiles.thenRun("eval number", """assert(eval(Num(3)) == 3)""")

    compiles.thenRun("eval sub", """
      assert(eval(Sub(Num(3), Num(10))) == -7)""")

    compiles.thenRun("eval div",
      """assert(eval(Div(Num(10), Num(2))) == 5)""")

    compiles.thenRun("eval nested",
      """assert(eval(Add(Num(2), Mul(Num(3), Num(4)))) == 14)""")

  }
}
