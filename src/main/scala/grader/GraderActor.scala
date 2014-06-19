package cs220.submission.grader

import scala.concurrent._
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.pipe
import java.nio.file.{Files, Paths}
import org.apache.commons.io.FileUtils
import scala.async.Async.{async, await}
import scala.util.{Try, Success, Failure}
import plasma.docker._
import java.util.concurrent.TimeoutException
import cs220.submission.messages._

class GraderActor(config : GraderConfig) extends Actor with ActorLogging {

  // The execution context of futures within this actor
  import context.dispatcher

  private def runInContainer(files : Map[String, Array[Byte]],
                             image : String,
                             command : List[String],
                             memoryBytes : Long,
                             timeout : Duration) : Future[SubmitResult] = async {
    val dir = Files.createTempDirectory(config.tmpDir, "GraderActor")
      .toAbsolutePath()

    await(Future({
      for ((file, data) <- files) {
        val path = dir.resolve(file)
        FileUtils.forceMkdir(path.getParent.toFile)
        Files.write(path, data)
      }}))

    val docker = new Docker(config.dockerUrl)

    val conf = container(image)
      .withMountPoint("/data")
      .withWorkingDir("/data")
      .withCommand(command : _*)

    val ctr = await(docker.createContainer(conf))
    val id = ctr.Id
    val hostConf = HostConfig.empty.bindVolume(dir.toString, "/data")
    await(docker.startContainer(id, hostConf))
    val waitCode = Try(Await.result(docker.waitContainer(id), timeout))
    val stdout = new String(await(docker.logs(id, false)))
    val stderr = new String(await(docker.logs(id, true)))
    await(Future({
      docker.deleteContainer(id, true, true)
      FileUtils.deleteDirectory(dir.toFile)
    }))
    waitCode match {
      case Failure(exn) => DidNotFinish(stdout, stderr)
      case Success(code) => Complete(code, stdout, stderr)
    }
  }

  def receive = {
    case Submit(files, image, command, memLimit, timeLimit) => {
      runInContainer(files, image, command, memLimit, timeLimit) pipeTo sender
    }
  }

}