package cs220.submission

import java.nio.file.Path
import scala.concurrent.duration.Duration

abstract trait ExpectedException

class InvalidSubmission(message : String) extends Throwable(message) with ExpectedException {

  override def getMessage() = message

}

class InvalidAssignment(message : String) extends Throwable(message) with ExpectedException {

  override def getMessage() = message

}


trait Assignment {

  val name : String
  val step : String

  // Copies submitted files from fromDir into toDir. Creates any boilerplate
  // needed to run the code. Fails if submission in fromDir is malformed.
  def prepareSubmission(fromDir : Path, toDir : Path) : Unit

}