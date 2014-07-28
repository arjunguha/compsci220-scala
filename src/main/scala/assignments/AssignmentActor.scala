package cs220.submission.assignments

import akka.actor.{Actor, ActorLogging, ActorRef}
import java.nio.file.{Paths, Files, DirectoryStream, Path}
import scala.concurrent.duration._
import cs220.submission.messages._

class AssignmentActor(config : AssignmentActorConfig)
  extends Actor with ActorLogging {

  import scala.collection.JavaConversions._

  def isDirectoryFilter = new DirectoryStream.Filter[Path] {
    override def accept(entry : Path) = Files.isDirectory(entry)
  }

  val assignments : Map[(String, String), AssignmentSettings] = {
    val stream = Files.newDirectoryStream(config.assignmentsDir)
    val results = stream.flatMap({ name =>
      if (Files.isDirectory(name)) {
        val stream = Files.newDirectoryStream(name, isDirectoryFilter)
        val results = stream.map { step =>
          val config = AssignmentSettings(step.resolve("assignment.conf"))
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

  def receive = { case _ => () }

    // case GetAssignmentMetadata(name, step) =>
    //   assignments.get(name -> step) match {
    //     case scala.None => sender ! InvalidAssignment(name, step)
    //     case Some(asgn) => {
    //       sender ! AssignmentMetadata(name, step, asgn.submit,
    //          asgn.boilerplateHashes, asgn.command, asgn.image,
    //          asgn.timeLimit, asgn.memoryLimit)
    //     }
    //   }
    // case GetAssignmentBoilerplate(name, step) => {
    //   assignments.get(name -> step) match {
    //     case None => sender ! InvalidAssignment(name, step)
    //     case Some(asgn) =>
    //       sender ! AssignmentBoilerplate(name, step, asgn.boilerplateData)
    //   }
    // }


}