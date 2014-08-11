package cs220.submission.messages

import scala.concurrent.duration._

case class GetAssignmentMetadata(name : String, step : String)

sealed trait MetadataResult

case class InvalidAssignment(name : String, step : String)
  extends MetadataResult

case class AssignmentMetadata(
  name : String,
  step : String,
  submit : List[String],
  boilerplate : List[(String, String)], // filename and hash
  command : List[String],
  image : String,
  timeLimit : Duration,
  memoryLimit : Long)
  extends MetadataResult

case class GetAssignmentBoilerplate(name : String, step : String)

case class AssignmentBoilerplate(
  name : String,
  step : String,
  boilerplate : List[(String, Array[Byte])])

case class Submit(files : Map[String, Array[Byte]],
                  image : String,
                  command : List[String],
                  memoryLimit : Long,
                  timeLimit : Duration)

sealed trait SubmitResult

case class DidNotFinish(stdout : String, stderr : String) extends SubmitResult
case class Complete(code : Int, stdout : String, stderr : String) extends SubmitResult

case class SubmitLocal(asgn : String, step : String, dir : String)
