class ProvidedTests extends org.scalatest.FunSuite {

  import java.nio.file.{Paths, Files}
  import PathImplicits._

  test("It would be convenient if we could build a path by using / to separate strings") {
    assert("usr" / "bin" / "scala" == Paths.get("usr/bin/scala"))
  }

  test("The / operator should be able to put two paths together too") {
    val p1 = "usr" / "local"
    val p2 = "bin" / "scala"
    assert(p1/p2 == Paths.get("usr/local/bin/scala"))
  }

  test("Paths should have a .write method to create files") {
    val p = Paths.get("test.txt")
    val contents = "This should write a file"
    try {
      p.write(contents)
      assert(new String(Files.readAllBytes(p)) == contents)
    }
    finally {
      Files.deleteIfExists(p)
    }
  }

  test("Paths should have a .read method to read files") {
    val p = Paths.get("test.txt")
    val contents = "This should write a file"
    try {
      Files.write(p, contents.getBytes)
      assert(p.read() == contents)
    }
    finally {
      Files.deleteIfExists(p)
    }
  }

  test("Paths should have a .append method to append data to the end of a file") {
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
  }

  import TimeImplicits._
  import java.time.LocalDate

  test("This is a convenient way of writing a date in the current year") {
    assert(15.jan == LocalDate.of(2016, 1, 15))
    assert(29.feb == LocalDate.of(2016, 2, 29))
    assert(2.mar == LocalDate.of(2016, 3, 2))
    intercept[Exception] {
      val bad = 30.feb
    }
  }

  test("We can write dates in other years like this") {
    val date1 = 28 feb 2015
    assert(date1 == LocalDate.of(2015, 2, 28))
    val date2 = 15 oct 1989
    assert(date2 == LocalDate.of(1989, 10, 15))
  }

  test("We can add days to a date") {
    val date1 = LocalDate.of(2016, 1, 31) + 2.days
    assert(date1 == LocalDate.of(2016, 2, 2))
  }

  test("We can add days to a date written using our date DSL") {
    val date1 = (31 jan 2016) + 2.days
    assert(date1 == LocalDate.of(2016, 2, 2))
  }

  test("We can add months to a date") {
    val date1 = LocalDate.of(2016, 1, 31) + 3.months
    assert(date1 == LocalDate.of(2016, 4, 30))
  }

  test("We can add years to a date") {
    val date1 = LocalDate.of(2016, 1, 31) + 5.years
    assert(date1 == LocalDate.of(2021, 1, 31))
  }



}