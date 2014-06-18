package cs220.submission.grader

import scala.concurrent.duration._
import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import java.util.concurrent.TimeUnit

class GraderConfig(config : Config) {

  val tmpDir = Paths.get(config.getString("temp-dir"))

  val dockerUrl = config.getString("docker-url")

  val hardTimeout =
    config.getDuration("hard-timeout", TimeUnit.MILLISECONDS).milliseconds

}
