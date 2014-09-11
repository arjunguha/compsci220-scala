package plasma.grader.top

import scala.concurrent.duration._
import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import java.util.concurrent.TimeUnit

class TopSettings(base : Path, config : Config) {

  val assignmentDir = base.resolve(Paths.get(config.getString("assignments")))
  val dockerUrl = config.getString("dockerUrl")
  val tmpDir = base.resolve(Paths.get(config.getString("tmpDir")))

}

object TopSettings {

  def apply(path : Path) = {
    // The error message from ConfigFactory.parseFile is very misleading.
    if (!Files.isRegularFile(path)) {
      throw new IllegalArgumentException(s"file not found: $path")
    }

    val parent = path.toAbsolutePath().getParent()
    new TopSettings(parent, ConfigFactory.parseFile(path.toFile).resolve())
  }

}