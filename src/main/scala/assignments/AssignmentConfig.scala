package cs220.submission.assignments

import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import java.util.concurrent.TimeUnit
import org.apache.commons.codec.digest.DigestUtils
import scala.concurrent.duration._

case class ConfigException(message : String) extends Throwable

class AssignmentConfig(base : Path, config : Config) {

  import scala.collection.JavaConversions._

  val name = config.getString("name")
  val step = config.getString("step")
  val memoryLimit = config.getBytes("memory-limit")
  val timeLimit = config.getDuration("time-limit", TimeUnit.MILLISECONDS).milliseconds
  val submit = config.getStringList("submit").map(asSubPath _).toList
  val command = config.getStringList("command").toList
  val image = config.getString("image")

  // TODO(arjun): files to submit and boilerplate should not overlap

  /** List of path, file contents, and MD5 hash */
  val boilerplate : List[(String, Array[Byte], String)] =
    config.getStringList("boilerplate").map(asSubPath _).map(loadBoilerplate)
      .toList

  private def asSubPath(str: String) : String = {
    val resolvedPath = base.resolve(Paths.get(str))
    if (!resolvedPath.startsWith(base)) {
      throw new IllegalArgumentException(s"the path ${str} must be a sub-path")
    }
    str
  }

  private def loadBoilerplate(str : String) : (String, Array[Byte], String) = {
    val path = base.resolve(Paths.get(str))
    val data = Files.readAllBytes(path)
    val md5 = DigestUtils.md5Hex(data)
    (str, data, md5)
  }

  val boilerplateHashes = boilerplate.map { x => (x._1 -> x._3) }
  val boilerplateData = boilerplate.map { x => (x._1 -> x._2) }
}

object AssignmentConfig {

  def apply(path: java.nio.file.Path) = {
    assert(Files.isRegularFile(path))

    val defaults = ConfigFactory.load("assignmentDefaults").resolve()
    val _ = defaults.getStringList("boilerplate")
    val config = ConfigFactory.parseFile(path.toFile)
      .resolve().withFallback(defaults)
    // getParent should always succeed since isRegularFile(path) holds
    new AssignmentConfig(path.getParent(), config)
  }

}