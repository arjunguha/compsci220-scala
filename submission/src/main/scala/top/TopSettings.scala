package cs220.submission.top

import scala.concurrent.duration._
import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import java.util.concurrent.TimeUnit

class TopSettings(config : Config) {

  val assignmentDir = Paths.get(config.getString("assignments"))
  val dockerUrl = config.getString("dockerUrl")
  val tmpDir = Paths.get(config.getString("tmpDir"))

}

object TopSettings {

  def apply(path : Path) = {
    new TopSettings(ConfigFactory.parseFile(path.toFile).resolve())
  }

}