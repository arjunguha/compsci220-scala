package grading



object Worker {

  import java.nio.file.{Files, Paths, Path}
  import com.spotify.docker.client.DockerClient
  import com.spotify.docker.client.messages.{HostConfig, ContainerConfig}
  import org.apache.commons.io.FileUtils

java.nio.file.Files.
  def run(docker: DockerClient, image: String, timeoutSeconds: Int, zippedVolumes: Map[String, Array[Byte]]) = {
    val root = Files.createTempDirectory(null)
    try {

      val volumes = zippedVolumes.toList.map {
        case (containerPath, volume) => {
          val hostPath = Files.createTempDirectory(root, null)

        }
      }

      docker.pull(image)
      val containerConfig = ContainerConfig.builder
        .image(image)
        .vo
      val r = docker.createContainer(ContainerConfig.builder.image(image).build)
    }
    finally {
      FileUtils.deleteDirectory(root.toFile)
    }
  }

}
