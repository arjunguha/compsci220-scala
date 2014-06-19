package cs220.submission.student

import com.typesafe.config.ConfigFactory
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.{Paths, Files, Path}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import java.io.File
import scala.async.Async.{async, await}
import akka.pattern.{pipe, ask}
import cs220.submission.messages._
import org.apache.commons.codec.digest.DigestUtils

object Main extends App {


  val confFile = System.getenv("SUBMISSION_CONF")
  if (confFile == null) {
    println("Configuration error: SUBMISSION_CONF environment variable missing.")
    System.exit(1)
  }

  val config = ConfigFactory.parseFile(new File(confFile)).resolve()
  val graderConf = new cs220.submission.grader.GraderConfig(config.getConfig("grader"))
  val assignmentsConf = new cs220.submission.assignments.AssignmentActorConfig(config.getConfig("assignments"))

  implicit val system = ActorSystem("student")
  implicit val log = system.log
  import system.dispatcher

  implicit val timeout = Timeout(60.second)

  lazy val grader =
    system.actorOf(Props(classOf[cs220.submission.grader.GraderActor],
      graderConf))

  lazy val asgns =
    system.actorOf(Props(classOf[cs220.submission.assignments.AssignmentActor],
      assignmentsConf))

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
        val r = await((grader ? Submit(allFiles, image, command, memLimit, timeLimit)).mapTo[SubmitResult])
        log.debug("Got response")
        r
      }
    }
  }

  try {
    args match {
      case Array("submit", asgn, step, dir) => {
        println(Await.result(submit(asgn, step, dir), Duration.Inf))
      }
      case Array("submit", asgn, step) => {
        println(Await.result(submit(asgn, step, "."), Duration.Inf))
      }
    }
  }
  finally {
    system.shutdown
    System.exit(0)
  }


}