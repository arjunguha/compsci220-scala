package grading

class HW1Grading(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add(s"object Grading extends App { import Lecture1._; $body }".getBytes, "src/main/scala/GradingMain.scala")
  }


  def body(root: TestCase): Unit = {

    val compiles = root.thenCompile("Does it compile", "")

    val removeZeroes = compiles.thenCompile("removeZeroes have the right type?",
      "val x: List[Int] = Lecture1.removeZeroes(List[Int]())")

    removeZeroes.thenRun("removeZeroes with only zeroes",
        """assert(removeZeroes(List(0, 0, 0, 0)) == Nil)""")

    removeZeroes.thenRun("removeZeroes preserves non-zero values",
        """assert(removeZeroes(List(0, 1, 0, 2, 0)) == List(1, 2))""")

    val countEvens = compiles.thenCompile("Does countEvens have the right type?",
      "val x: Int = countEvens(List[Int]())")

    countEvens.thenRun("countEvens on an empty list", """assert(countEvens(Nil) == 0)""")

    countEvens.thenRun("countEvens with no even numbers", """assert(countEvens(List(1, 3, 5, 7)) == 0)""")

    countEvens.thenRun("countEvens with only even numbers", """assert(countEvens(List(2, 4, 6, 8)) == 4)""")

    countEvens.thenRun("countEvens with a mixed list of numbers",
      """assert(countEvens(List(1, 3, 2, 5, 7, 2, 9, 2)) == 3)""")

    val removeAlternating = compiles.thenCompile("Does removeAlternating have the right type?",
        "val x: List[String] = removeAlternating(List[String]())")

    removeAlternating.thenRun("removeAlternating on an empty list", """assert(removeAlternating(Nil) == Nil)""")
    removeAlternating.thenRun("removeAlternating on a singleton list", """assert(removeAlternating("1" :: Nil) == "1" :: Nil)""")
    removeAlternating.thenRun("removeAlternating on a long list", """assert(removeAlternating(List("1","2","3","4","5")) == List("1","3","5"))""")

    val isAscending = compiles.thenCompile("Does isAscending have the right type?",
      "val x: Boolean = isAscending(List[Int]())")

    isAscending.thenRun("isAscending on an empty list", """assert(isAscending(Nil) == true)""")
    isAscending.thenRun("isAscending on an ascending list", """assert(isAscending(List(1, 3, 7, 100)) == true)""")
    isAscending.thenRun("isAscending on a non-ascending list", """assert(isAscending(List(1, 3, 5, 2)) == false)""")
    isAscending.thenRun("isAscending on a list with duplicates", """assert(isAscending(List(1, 1, 1, 1, 2)) == true)""")

    val addSub = compiles.thenCompile("Does addSub have the right type?",
      "val x: Int = addSub(List[Int]())")

    addSub.thenRun("addSub on an empty list", """assert(addSub(Nil) == 0)""")
    addSub.thenRun("addSub on a singleton list", """assert(addSub(List(10)) == 10)""")
    addSub.thenRun("addSub on a three-element list", """assert(addSub(List(10, 5, 1)) == 6)""")

    val fromTo = compiles.thenCompile("Does fromTo have the right type?",
        "val x: List[Int] = fromTo(0, 1)")

    fromTo.thenRun("fromTo(10, 11)", """assert(fromTo(10, 11) == List(10))""")
    fromTo.thenRun("fromTo(5, 10)", """assert(fromTo(5, 10) == List(5, 6, 7, 8, 9))""")

    val insertOrdered = compiles.thenCompile("Does insertOrdered have the right type?",
       "val x: List[Int] = insertOrdered(0, List[Int]())")

    insertOrdered.thenRun("insertOrdered into empty list",
      """assert(insertOrdered(10, Nil) == List(10))""")
    insertOrdered.thenRun("insertOrdered into head",
      """assert(insertOrdered(5, List(6, 7, 8)) == List(5,6,7,8))""")
    insertOrdered.thenRun("insertOrdered into last position",
        """assert(insertOrdered(200, List(6, 7, 8)) == List(6,7,8,200))""")
    insertOrdered.thenRun("insertOrdered into mid position",
        """assert(insertOrdered(7, List(6, 8)) == List(6,7,8))""")

    val sort = compiles.thenCompile("Does sort have the right type?",
       "val x: List[Int] = sort(List[Int]())")

    sort.thenRun("sort an empty list", """assert(sort(Nil) == Nil)""")
    sort.thenRun("sort non-empty list", """assert(sort(List(5, 4, 1, 2, 3)) == List(1, 2, 3, 4, 5))""")

    val sumDouble = compiles.thenCompile("Does sumDouble have the right type?",
       "val x:Int = Lecture1.sumDouble(List[Int]())")

    sumDouble.thenRun("sumDouble(Nil)", "assert(sumDouble(Nil) == 0)")
    sumDouble.thenRun("sumDouble on a non-empty list",  "assert(sumDouble(List(2, 2, 3, 7)) == 28)")
  }
}
