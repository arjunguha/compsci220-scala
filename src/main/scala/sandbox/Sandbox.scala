package cs220.submission.sandbox

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.{Files, Paths, Path}
import org.apache.commons.io.FileUtils
import scala.async.Async.{async, await}
import scala.util.{Try, Success, Failure}
import plasma.docker._
import java.util.concurrent.TimeoutException

sealed trait SandboxResult
case class DidNotFinish(stdout : String, stderr : String) extends SandboxResult
case class Complete(code : Int, stdout : String, stderr : String) extends SandboxResult

private object Sandbox {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[Sandbox])
}

/** Requires local access to Docker.
 *
 */
class Sandbox(dockerUrl : String) {

  /**
   * @param workingDir directory on the host to mount as R/W in the container
   * @param mountPoint mount point on the container for absWorkDir
   * @param image the image to load
   */
  def apply(workingDir : Path,
            mountPoint : String,
            image : String,
            command : List[String],
            memoryBytes : Long,
            timeout : Duration)
    (implicit ec : ExecutionContext) : Future[SandboxResult] = async {



    val absWorkDir = workingDir.toAbsolutePath()
    val docker = new Docker(dockerUrl)

    // TODO(arjun): memory limit?
    val conf = container(image)
      .withNetwork(true)
      .withMountPoint(mountPoint)
      .withWorkingDir(mountPoint)
      .withCommand(command : _*)
      .copy(Hostname = "vm")

    val hostConf = HostConfig.empty
      .bindVolume(absWorkDir.toString, mountPoint)

    // TODO(arjun): Failure?
    Sandbox.logger.info(s"Creating container")
    val ctr = await(docker.createContainer(conf))
    val id = ctr.Id
    Sandbox.logger.info(s"Starting container (id: $id)")
    await(docker.startContainer(id, hostConf))
    Sandbox.logger.info(s"Waiting for container (timeout: $timeout, id: $id)")
    val waitCode = Try(Await.result(docker.waitContainer(id), timeout))
    val stdout = new String(await(docker.logs(id, false)))
    val stderr = new String(await(docker.logs(id, true)))
    await(docker.deleteContainer(id, true, true))
    Sandbox.logger.info(s"Result: $waitCode")
    waitCode match {
      case Failure(exn) => DidNotFinish(stdout, stderr)
      case Success(code) => Complete(code, stdout, stderr)
    }
  }

}