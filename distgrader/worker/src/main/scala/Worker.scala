package grading

import java.io.ByteArrayInputStream

import com.spotify.docker.client.DockerClient.ExecStartParameter


object Worker {

  import java.nio.file.{Files, Paths, Path}
  import java.util.zip.ZipInputStream
  import com.spotify.docker.client.DockerClient
  import com.spotify.docker.client.messages.{HostConfig, ContainerConfig}
  import org.apache.commons.io.FileUtils
  import Messages.ContainerExit

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
          command: Seq[String],
          zippedVolumes: Map[String, Array[Byte]]) = {
    val root = Files.createTempDirectory(null)
    try {

      val binds = zippedVolumes.toList.map {
        case (containerPath, volume) => {
          val stream = new ByteArrayInputStream(volume)
          val zip = new ZipInputStream(stream)
          val hostPath = Files.createTempDirectory(root, null)
          zip.unzipTo(hostPath)
          zip.close()
          //println(Files.list(hostPath).toArray.toList.map(_.asInstanceOf[Path].getFileName))
          (hostPath, containerPath)
        }
      }


      docker.pull(image)
      val containerConfig = ContainerConfig.builder
        .image(image)
        .memory(1024L * 1024L)
        .attachStderr(true)
        .attachStdin(false)
        .attachStdout(false)
        .cmd(command: _*).build
      val container = docker.createContainer(containerConfig)

      // This probably works, but copyToContainer requires the directories to be on the Docker host.
      for ((hostPath, containerPath) <- binds) {
        println(Files.list(hostPath).toArray.toList.map(_.asInstanceOf[Path].getFileName))
        docker.copyToContainer(hostPath, container.id, containerPath)
      }

      docker.startContainer(container.id)



      val exit = docker.waitContainer(container.id).statusCode

      val stdout = loan(docker.logs(container.id, DockerClient.LogsParam.stdout))(_.readFully)
      val stderr = loan(docker.logs(container.id, DockerClient.LogsParam.stderr))(_.readFully)

      docker.removeContainer(container.id, true)
      ContainerExit(exit, stdout, stderr)

      // docker.wait(timeoutSeconds * 1000L)
    }
    finally {
      FileUtils.deleteDirectory(root.toFile)
    }
  }

}
