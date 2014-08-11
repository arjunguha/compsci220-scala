package cs220.submission.assignments

import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import java.util.concurrent.TimeUnit
import org.apache.commons.codec.digest.DigestUtils
import scala.concurrent.duration._
import cs220.submission._

case class ConfigException(message : String) extends Throwable

// Specifies the name and step of an assignment, the boilerplate code, and
// the files that must be present in a valid submission.
class AssignmentSettings(base : Path, config : Config) {

  import scala.collection.JavaConversions._

  val name = config.getString("name")
  val step = config.getString("step")
  val submit = config.getStringList("submit").toList

  // TODO(arjun): files to submit and boilerplate should not overlap

  assertAllSubPaths(base, submit)
  assertAllSubPaths(base, config.getStringList("boilerplate"))

  /** List of path, file contents, and MD5 hash */
  val boilerplate : List[(String, Array[Byte], String)] =
    config.getStringList("boilerplate").map(loadBoilerplate).toList

  private def loadBoilerplate(str : String) : (String, Array[Byte], String) = {
    val path = base.resolve(Paths.get(str))
    val data = Files.readAllBytes(path)
    val md5 = DigestUtils.md5Hex(data)
    (str, data, md5)
  }

  val boilerplateHashes = boilerplate.map { x => (x._1 -> x._3) }
  val boilerplateData = boilerplate.map { x => (x._1 -> x._2) }
}

object AssignmentSettings {

  def apply(path: java.nio.file.Path) = {
    assert(Files.isRegularFile(path))

    val defaults = ConfigFactory.load("assignmentDefaults").resolve()
    val _ = defaults.getStringList("boilerplate")
    val config = ConfigFactory.parseFile(path.toFile)
      .resolve().withFallback(defaults)
    // getParent should always succeed since isRegularFile(path) holds
    new AssignmentSettings(path.getParent(), config)
  }

}