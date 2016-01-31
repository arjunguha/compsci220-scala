package grading

import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

import com.spotify.docker.client.DockerClient.ExecStartParameter
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

import scala.concurrent.ExecutionContext


object Worker {

  import java.nio.file.{Files, Paths, Path}
  import java.util.zip.ZipInputStream
  import com.spotify.docker.client.DockerClient
  import com.spotify.docker.client.messages.{HostConfig, ContainerConfig}
  import org.apache.commons.io.FileUtils
  import Messages.ContainerExit
  import scala.concurrent._
  import scala.concurrent.duration._

  import edu.umass.cs.zip.Implicits._

  def loan[A <: AutoCloseable, B](resource: A)(body: A => B): B = {
    try {
      body(resource)
    }
    finally {
      resource.close
    }
  }


  def run(docker: DockerClient, image: String, timeoutSeconds: Int,
          workingDir: String,
          command: Seq[String],
          zippedVolumes: Map[String, Array[Byte]])(implicit ex: ExecutionContext) = {
    import scala.collection.JavaConversions._

    val root = Files.createTempDirectory(null)
    try {

      val binds = zippedVolumes.toList.map {
        case (containerPath, volume) => {
          val stream = new ByteArrayInputStream(volume)
          val zip = new ZipInputStream(stream)
          val hostPath = Files.createTempDirectory(root, null)
          zip.unzipTo(hostPath)
          zip.close()
          (hostPath, containerPath)
        }
      }


      val containerConfig = ContainerConfig.builder
        .image(image)
        .memory(1024L * 1024L)
        .attachStderr(true)
        .attachStdin(false)
        .attachStdout(false)
        .cmd(command: _*)
        .workingDir(workingDir)
        .build

      val container = docker.createContainer(containerConfig)

      for ((hostPath, containerPath) <- binds) {
        println(Files.list(hostPath).toArray.toList.map(_.asInstanceOf[Path].getFileName))
        // docker.
        docker.copyToContainer(hostPath, container.id, containerPath)
      }


      docker.startContainer(container.id)

      try {
        Await.result(Future {
          val exit = docker.waitContainer(container.id).statusCode
          val stdout = loan(docker.logs(container.id, DockerClient.LogsParam.stdout))(_.readFully)
          val stderr = loan(docker.logs(container.id, DockerClient.LogsParam.stderr))(_.readFully)
          docker.removeContainer(container.id, true)
          ContainerExit(exit, stdout, stderr)
        }, timeoutSeconds.seconds)
      }
      catch {
        case exn: TimeoutException => {
          docker.killContainer(container.id)
          docker.removeContainer(container.id, true)
          ContainerExit(-1, s"Program did not terminate after $timeoutSeconds seconds", "")
        }
      }
    }
    finally {
      FileUtils.deleteDirectory(root.toFile)
    }
  }

}
