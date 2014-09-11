import org.scalatest.FunSuite
import plasma.grader.yamlassignment.YamlAssignment
import java.nio.file.{Files, Paths}
import TestBoilerplate._
import org.yaml.snakeyaml.error.YAMLException

class YamlAssignmentParserSuite extends FunSuite {

  forallFiles(s"./submission/src/test/files/YamlAssignmentParserSuite/good") { path =>

    test(s"$path parses") {
      YamlAssignment("fake-assignment-name", "fake-assignment-step",
                     path, Paths.get("."))
    }

  }

}