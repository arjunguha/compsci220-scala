package grading

class GradeRegexes(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix =
    """
      import Regexes._

      def check(re: scala.util.matching.Regex, str: String): Boolean = {
        re.pattern.matcher(str).matches()
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

    compiles.thenRun("notAlphanumeric -- symbols and spaces",
      """
      assert(check(notAlphanumeric, " #$!$$"))
      """)

    compiles.thenRun("notAlphanumeric -- letters and numbers",
      """
      assert(!check(notAlphanumeric, "kmafnFSF2323"))
      """)

    compiles.thenRun("notAlphanumeric -- mix of letters and symbols",
      """
      assert(check(notAlphanumeric, " #$!asdsd$$") == false)
      """)

    compiles.thenRun("time -- midnight",
      """
      assert(check(time, "00:00") == true)
      """)

    compiles.thenRun("time -- after noon",
      """
      assert(check(time, "15:34") == true)
      """)

    compiles.thenRun("time -- bad minute",
      """
      assert(check(time, "15:60") == false)
      """)

    compiles.thenRun("time -- bad hour",
      """
      assert(check(time, "25:01") == false)
      """)

    compiles.thenRun("phone number -- okay",
      """
      assert(check(phone, "(641) 275-1531"))
      """)

    compiles.thenRun("phone number -- bad paren",
      """
      assert(check(phone, "(641( 275-1531") == false)
      """)

    compiles.thenRun("phone number -- letters",
      """
      assert(check(phone, "(64A) 275-1531") == false)
      """)

    compiles.thenRun("zip -- five digit",
      """
      assert(check(zip, "02915"))
      """)

    compiles.thenRun("zip -- nine digit",
      """
      assert(check(zip, "02915-9232"))
      """)

    compiles.thenRun("zip -- too long",
      """
      assert(check(zip, "02915-92322") == false)
      """)

    compiles.thenRun("comment -- short",
      """
      assert(check(comment, "/* */"))
      """)

    compiles.thenRun("comment -- /**********/",
      """
      assert(check(comment, "/****************/"))
      """)

    compiles.thenRun("comment -- no closing *",
      """
      assert(check(comment, "/* /") == false)
      """)

    compiles.thenRun("roman -- four Is",
      """
      assert(check(roman, "IIII") == false)
      """)

    compiles.thenRun("roman -- four Is after V",
      """
      assert(check(roman, "VIIII") == false)
      """)

    compiles.thenRun("roman -- LL",
      """
      assert(check(roman, "LL") == false)
      """)

    compiles.thenRun("roman -- XV",
      """
      assert(check(roman, "XV") == true)
      """)

    compiles.thenRun("date -- not leap year",
      """
      assert(check(date, "2015-02-29") == false)
      """)

    compiles.thenRun("date -- leap year",
      """
      assert(check(date, "2016-02-29") == true)
      """)

    compiles.thenRun("date -- month ok",
      """
      assert(check(date, "2015-03-31") == true)
      """)

    compiles.thenRun("date -- month zero",
      """
      assert(check(date, "2015-00-01") == false)
      """)

    compiles.thenRun("date -- month bad",
      """
      assert(check(date, "2015-04-31") == false)
      """)

    compiles.thenRun("evenParity -- even",
      """
      assert(check(evenParity, "222222222222222") == true)
      """)

    compiles.thenRun("evenParity -- odd",
      """
      assert(check(evenParity, "9999999") == false)
      """)

    compiles.thenRun("evenParity -- odd can sum to even",
      """
      assert(check(evenParity, "9991") == true)
      """)




  }
}