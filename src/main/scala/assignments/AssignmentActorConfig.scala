package cs220.submission.assignments

import com.typesafe.config.{ConfigFactory, Config}
import java.nio.file.{Paths, Files, Path}
import org.apache.commons.codec.digest.DigestUtils

class AssignmentActorConfig(config : Config) {

  val assignmentsDir = Paths.get(config.getString("assignmentsDirectory"))

  if (!Files.isDirectory(assignmentsDir)) {
    throw new IllegalArgumentException(s"$assignmentsDir is not a directory")
  }

}
