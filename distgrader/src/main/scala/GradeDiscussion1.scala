package grading

class Discussion1Grading(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add(s"object GradingMain extends App { import Exercises._; $body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Check that object Exercises is defined", "()")

    val exercise1 = compiles.thenCompile("Does exercise1 have the right type?",
       """def foo(lst: List[Bird]): List[String] = exercise1(lst)""", score = 0)

    exercise1.thenRun("Does exercise1 work on the empty list?",
      """assert(exercise1(Nil) == Nil)""", score = 0)

    exercise1.thenRun("Does exercise1 work on the given example?",
      """assert(exercise1(List(Duck(), Duck(), Goose())) == List("dog food", "dog food", "pate"))""", score = 0)

    exercise1.thenRun("Does exercise1 work on a different example?",
      """assert(exercise1(List(Duck(), Duck(), Goose(), Goose(), Goose())) == List("dog food", "dog food", "pate", "pate", "pate"))""", score = 0)
  }
}
