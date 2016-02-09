import Exercises._
import org.scalatest.FunSuite

class ExerciseTests extends FunSuite {
  def testMaker[I,O](name: String, input: I, output: O, func: I => O) = {
    test(s"${name} takes ${input} and correctly returns ${output}") {
      assert(func(input) == output)
    }
  }
  
  // exercise 2
  val ex1input1 = List(Duck(), Duck(), Goose())
  val ex1output1 = List("dog food", "dog food", "pate")
  testMaker("exercise1", ex1input1, ex1output1, exercise1)
}