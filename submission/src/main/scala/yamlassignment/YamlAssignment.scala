package cs220.submission.yamlassignment

import cs220.submission.{Assignment, InvalidSubmission}
import java.nio.file.{Files, Path}
import scala.concurrent.duration.Duration

private class YamlAssignment(
  val name : String,
  val step : String,
  boilerplateDir : Path,
  parsed : AssignmentBean)
  extends Assignment {

  import scala.collection.JavaConversions._

  // Copies submitted files from fromDir into toDir. Creates any boilerplate
  // needed to run the code. Fails if submission in fromDir is malformed.
  def prepareSubmission(fromDir : Path, toDir : Path) : Unit = {

    for (expected <- parsed.getExpected) {
      val submitted = fromDir.resolve(expected)
      if (!Files.isRegularFile(submitted)) {
        throw new InvalidSubmission(s"expected file $expected")
      }
      Files.copy(submitted, toDir.resolve(expected))
    }

    for (file <- parsed.getBoilerplate) {
      Files.copy(boilerplateDir.resolve(file), toDir.resolve(file))
    }

  }

}

object YamlAssignment {

  import org.yaml.snakeyaml._
  import org.yaml.snakeyaml.constructor.Constructor

  private val ctor = new Constructor(classOf[AssignmentBean])
  private val desc = new TypeDescription(classOf[AssignmentBean])
  desc.putListPropertyType("expected", classOf[String])
  desc.putListPropertyType("boilerplate", classOf[String])
  ctor.addTypeDescription(desc)
  private val yaml = new Yaml(ctor)

  // Path to assignment directory. Must have assignment.yaml inside
  def apply(asgn : String,
            step : String,
            yamlFile : Path,
            boilerplateDir : Path) : Assignment = {

    val yamlData = new String(Files.readAllBytes(yamlFile))
    yaml.load(yamlData) match {
      case bean : AssignmentBean => {
        new YamlAssignment(asgn, step, boilerplateDir, bean)
      }
    }
  }

}