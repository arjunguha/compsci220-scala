package cs220.submission.top

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
    val parent = path.toAbsolutePath().getParent()
    new TopSettings(parent, ConfigFactory.parseFile(path.toFile).resolve())
  }

}