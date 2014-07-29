import org.scalatest.FunSuite
import cs220.submission.yamlassignment.YamlAssignment
import java.nio.file.{Files, Paths}
import TestBoilerplate._
import org.yaml.snakeyaml.error.YAMLException

class YamlAssignmentParserSuite extends FunSuite {

  val base = "/home/vagrant/Hacking/Classes/cmpsci220/submission"

  forallFiles(s"${base}/src/test/files/YamlAssignmentParserSuite/good") { path =>

    test(s"$path parses") {
      YamlAssignment("fake-assignment-name", "fake-assignment-step",
                     path, Paths.get("."))
    }

  }

  forallFiles("./src/test/files/YamlAssignmentParserSuite/bad") { path =>

    test(s"$path should not parse") {
      intercept[YAMLException] {
      YamlAssignment("fake-assignment-name", "fake-assignment-step",
                     path, Paths.get("."))
      }
    }

  }

}