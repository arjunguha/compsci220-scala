package cs220.submission.assignments

import akka.actor.{Actor, ActorLogging, ActorRef}
import java.nio.file.{Paths, Files, DirectoryStream, Path}
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
  command : String,
  image : String,
  timeLimit : Duration,
  memoryLimit : Long)
  extends MetadataResult

case class GetAssignmentBoilerplate(name : String, step : String)

case class AssignmentBoilerplate(
  name : String,
  step : String,
  boilerplate : List[(String, Array[Byte])])


class AssignmentActor(config : AssignmentActorConfig)
  extends Actor with ActorLogging {

  import scala.collection.JavaConversions._

  def isDirectoryFilter = new DirectoryStream.Filter[Path] {
    override def accept(entry : Path) = Files.isDirectory(entry)
  }

  val assignments : Map[(String, String), AssignmentConfig] = {
    val stream = Files.newDirectoryStream(config.assignmentsDir)
    val results = stream.flatMap({ name =>
      if (Files.isDirectory(name)) {
        val stream = Files.newDirectoryStream(name, isDirectoryFilter)
        val results = stream.map { step =>
          val config = AssignmentConfig(step.resolve("assignment.conf"))
          // TODO(arjun): validate config.name and .step against params
          ((config.name -> config.step) -> config)
        }
        stream.close()
        results
      }
      else {
        log.warning(s"Skipping $name (expected an assignment directory)")
        List()
      }
    }).toMap
    stream.close()
    results
  }

  def receive = {
    case GetAssignmentMetadata(name, step) =>
      assignments.get(name -> step) match {
        case scala.None => sender ! InvalidAssignment(name, step)
        case Some(asgn) => {
          sender ! AssignmentMetadata(name, step, asgn.submit,
             asgn.boilerplateHashes, asgn.command, asgn.image,
             asgn.timeLimit, asgn.memoryLimit)
        }
      }
    case GetAssignmentBoilerplate(name, step) => {
      assignments.get(name -> step) match {
        case None => sender ! InvalidAssignment(name, step)
        case Some(asgn) =>
          sender ! AssignmentBoilerplate(name, step, asgn.boilerplateData)
      }
    }
  }

}