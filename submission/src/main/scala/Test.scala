package plasma.grader

import java.nio.file.Path
import scala.concurrent.duration.Duration

trait Test {

  val description : String
  val points : Int
  val memoryLimitBytes : Int
  val timeLimit : Duration

  // Create files in workingDir to run the test. Assume that the program to
  // be tested in already present.
  def prepare(workingDir : Path, asgn : Assignment) : Unit

  // Command to run in the sandbox
  val command : List[String]

  // Image to use to run the command
  val image : String

}