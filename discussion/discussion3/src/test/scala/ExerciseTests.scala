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
  
  // exercise 4.1
  val ex3input1 = List(Duck(), Duck(), Goose())
  val ex3output1 = 12
  testMaker("exercise3", ex3input1, ex3output1, exercise3)
  
  // exercise 4.2
  val ex3input2 = List(Goose(), Goose(), Goose(), Goose(), Duck(), Goose())
  val ex3output2 = 51
  testMaker("exercise3", ex3input2, ex3output2, exercise3)
  
  // exercise 4.3
  val ex3input3 = List()
  val ex3output3 = 0
  testMaker("exercise3", ex3input3, ex3output3, exercise3)
}