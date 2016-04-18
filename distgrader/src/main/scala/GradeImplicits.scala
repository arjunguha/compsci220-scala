package grading

class GradeImplicits(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix =
    """
      import java.nio.file.{Paths, Files}
      import java.time.LocalDate
    """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
      zip.add("""
        resolvers += "PLASMA" at "https://dl.bintray.com/plasma-umass/maven"
        libraryDependencies += "edu.umass.cs" %% "compsci220" % "1.0.1"
        """.getBytes,
        "build.sbt")
    zip.add(s"object GradingMain extends App { $prefix $body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Does the program compile?", "()")

    compiles.thenRun("It would be convenient if we could build a path by using / to separate strings", """
      import PathImplicits._
      assert("usr" / "bin" / "scala" == Paths.get("usr/bin/scala"))
    """)

    compiles.thenRun("The / operator should be able to put two paths together too", """
      import PathImplicits._
      val p1 = "usr" / "local"
      val p2 = "bin" / "scala"
      assert(p1/p2 == Paths.get("usr/local/bin/scala"))
    """)

    compiles.thenRun("Paths should have a .write method to create files", """
      import PathImplicits._
      val p = Paths.get("test.txt")
      val contents = "This should write a file"
      try {
        p.write(contents)
        assert(new String(Files.readAllBytes(p)) == contents)
      }
      finally {
        Files.deleteIfExists(p)
      }
    """)

    compiles.thenRun("Paths should have a .read method to read files", """
      import PathImplicits._
      val p = Paths.get("test.txt")
      val contents = "This should write a file"
      try {
        Files.write(p, contents.getBytes)
        assert(p.read() == contents)
      }
      finally {
        Files.deleteIfExists(p)
      }
    """)

    compiles.thenRun("Paths should have a .append method to append data to the end of a file", """
      import PathImplicits._
      val p = Paths.get("test.txt")
      val contents = "First line\nSecond"
      try {
        p.append("First line\n")
        p.append("Second line\n")
       assert(new String(Files.readAllBytes(p)) == "First line\nSecond line\n")
      }
      finally {
        Files.deleteIfExists(p)
      }
    """)

    compiles.thenRun("This is a convenient way of writing a date in the current year", """
      import DateImplicits._
      assert(15.jan == LocalDate.of(2016, 1, 15))
      assert(29.feb == LocalDate.of(2016, 2, 29))
      assert(2.mar == LocalDate.of(2016, 3, 2))
    """)

    compiles.thenRun("We can write dates in other years like this", """
      import DateImplicits._
      val date1 = 28 feb 2015
      assert(date1 == LocalDate.of(2015, 2, 28))
      val date2 = 15 oct 1989
      assert(date2 == LocalDate.of(1989, 10, 15))
    """)

    compiles.thenRun("We can add days to a date","""
      import DateImplicits._
      val date1 = LocalDate.of(2016, 1, 31) + 2.days
      assert(date1 == LocalDate.of(2016, 2, 2))
    """)

    compiles.thenRun("We can add days to a date written using our date DSL", """
      import DateImplicits._
      val date1 = (31 jan 2016) + 2.days
      assert(date1 == LocalDate.of(2016, 2, 2))
    """)

    compiles.thenRun("We can add months to a date", """
      import DateImplicits._
      val date1 = LocalDate.of(2016, 1, 31) + 3.months
      assert(date1 == LocalDate.of(2016, 4, 30))
    """)

    compiles.thenRun("We can add years to a date", """
      import DateImplicits._
      val date1 = LocalDate.of(2016, 1, 31) + 5.years
      assert(date1 == LocalDate.of(2021, 1, 31))
    """)

  }
}