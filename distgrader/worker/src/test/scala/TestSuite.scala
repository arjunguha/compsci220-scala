import java.io.ByteArrayOutputStream
import java.util.zip.{ZipEntry, ZipOutputStream}

import com.spotify.docker.client.messages.AuthConfig

class TestSuite extends org.scalatest.FunSuite {

  import com.spotify.docker.client.DefaultDockerClient
  import grading._
  import java.nio.file._

  test("Private IP is parsed correctly") {
    val txt = """{"networkInterface":[{"accessConfiguration":[{"externalIp":"104.196.61.192","type":"ONE_TO_ONE_NAT"}],"ip":"10.240.0.3","network":"projects/780349874596/networks/default"}]}"""
    import upickle.default._
    import InstanceMetadata._
    assert(read[Network](txt).networkInterface.head.ip == "10.240.0.3")
  }

  test("can build docker client") {
    val docker =  DefaultDockerClient.fromEnv.build
    docker.version()
  }

  test("containers start correctly") {
    val docker = DefaultDockerClient.fromEnv.build()
    val result = Worker.run(docker, image = "busybox:latest",
                            timeoutSeconds = 10,
                            command = Seq("/bin/echo", "helloworld"), zippedVolumes = Map())
    assert (result.code == 0)
    assert(result.stdout == "helloworld\n")
    assert(result.stderr == "")
  }

  test("volumes mount correctly") {
    import scala.collection.JavaConversions._
    val zipPath = Files.createTempFile("testing", ".zip")
    Files.delete(zipPath) // empty file makes next line crash
    val fs = FileSystems.newFileSystem(java.net.URI.create(s"jar:file:$zipPath"), Map("create" -> "true"), null)
    Files.write(fs.getPath("/myfile.txt"), "hello world".getBytes)
    fs.close
    val zip = Files.readAllBytes(zipPath)
    //Files.delete(zipPath)
    println(zipPath)

    val docker = DefaultDockerClient.fromEnv.build()

    val result = Worker.run(docker, image = "busybox:latest",
      timeoutSeconds = 10,
    //command = Seq("/bin/ls", "/myvol"),
      command = Seq("/bin/cat", "/myvol/myfile.txt"),
      zippedVolumes = Map("/myvol" -> zip))

    println(result)
    assert (result.code == 0)
    assert(result.stdout == "hello world\n")
    assert(result.stderr == "")


  }

}
