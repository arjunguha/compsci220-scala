package cs220.submission

import java.nio.file.Path
import scala.concurrent.duration.Duration

class InvalidSubmission(message : String) extends RuntimeException(message)

trait Assignment {

  val name : String
  val step : String

  // Copies submitted files from fromDir into toDir. Creates any boilerplate
  // needed to run the code. Fails if submission in fromDir is malformed.
  def prepareSubmission(fromDir : Path, toDir : Path) : Unit

}