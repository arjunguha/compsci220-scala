package cs220.submission.student

import cs220.submission._
import messages._
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.{Paths, Files, Path}
import akka.actor.{ActorRef, ActorSystem, Props, Actor, ActorLogging}
import akka.util.Timeout
import java.io.File
import scala.async.Async.{async, await}
import akka.pattern.{pipe, ask}
import org.apache.commons.codec.digest.DigestUtils


class StudentActor(grader : ActorRef, asgns : ActorRef)
  extends Actor with ActorLogging {

  import context.dispatcher

  implicit val timeout = Timeout(60.second)

  def readFile(base : Path, file : String) : Future[(String, Array[Byte])] =
    Future({
      val path = base.resolve(Paths.get(file))
      (file,  Files.readAllBytes(path))
    })

  def validateHash(base : Path, arg: (String, String)) : Future[Unit] =
    Future({
      val (file, expectedHash) = arg
      val path = base.resolve(Paths.get(file))
      val data = Files.readAllBytes(path)
      val md5 = DigestUtils.md5Hex(data)
      assert (md5 == expectedHash)
    })

  def submit(asgn : String, step : String, dir : String) : Future[Any] = async {
    await((asgns ? GetAssignmentMetadata(asgn, step)).mapTo[MetadataResult]) match {
      case InvalidAssignment(_, _) => {
        s"either $asgn is not an assignment or $step is not a step of $asgn"
      }
      case AssignmentMetadata(_, _, submit, boilerplate, command, image,
                              timeLimit, memLimit) => {
        println(s"Got metadata for $asgn/$step")
        val base = Paths.get(dir)
        await(Future.sequence(boilerplate.map(validateHash(base, _))))
        log.debug("Validated boilerplate hashes")
        val submitFiles = await(Future.sequence(submit.map(readFile(base, _))))
        log.debug("Read all files to submit")
        val boilerplateFiles = await((asgns ? GetAssignmentBoilerplate(asgn, step)).mapTo[AssignmentBoilerplate]).boilerplate
        log.debug("Prepared boilerplate, ready to submit...")
        val allFiles = (submitFiles ++ boilerplateFiles).toMap
        await((grader ? Submit(allFiles, image, command, memLimit, timeLimit)).mapTo[SubmitResult])
      }
    }
  }

  def receive = {
    case SubmitLocal(asgn, step, dir) => submit(asgn, step, dir).pipeTo(sender)
  }

}