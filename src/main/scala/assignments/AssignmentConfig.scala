package cs220.submission.assignments

import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import org.apache.commons.codec.digest.DigestUtils

class AssignmentConfig(base : Path, config : Config) {

  import scala.collection.JavaConversions._

  val name = config.getString("name")
  val step = config.getString("step")
  val submit = config.getStringList("submit").map(asSubPath _).toList
  val boilerplate = config.getStringList("boilerplate").map(asSubPath _).toList
  val command = config.getString("command")
  val image = config.getString("image")
  /** List of path, file contents, and MD5 hash */
  val boilerplateData : List[(String, Array[Byte], String)] =
    boilerplate.map(loadBoilerplate)

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