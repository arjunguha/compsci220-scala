import org.scalatest.FunSuite
import plasma.grader.yamltests.YamlTest
import java.nio.file.Files
import TestBoilerplate._
import org.yaml.snakeyaml.error.YAMLException

class YamlTestsParserSuite extends FunSuite {

  forallFiles("./submission/src/test/files/YamlTestsParserSuite/good") { path =>

    test(s"$path parses") {
      YamlTest(new String(Files.readAllBytes(path)))
    }

  }

  forallFiles("./submission/src/test/files/YamlTestsParserSuite/bad") { path =>

    test(s"$path should not parse") {
      intercept[YAMLException] {
        YamlTest(new String(Files.readAllBytes(path)))
      }
    }

  }

}