package cs220.submission.student

import cs220.submission._
import assignments.AssignmentActorConfig
import grader.GraderConfig
import com.typesafe.config.{Config, ConfigFactory}
import java.nio.file.{Paths, Files, Path}

class StudentSettings(config : Config) {

  val grader = new GraderConfig(config.getConfig("grader"))
  val assignments = new AssignmentActorConfig(config.getConfig("assignments"))

}

object StudentSettings {

  def apply(path : Path) = {
    new StudentSettings(ConfigFactory.parseFile(path.toFile).resolve())
  }

}