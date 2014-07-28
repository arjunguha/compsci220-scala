import org.scalatest.FunSuite

import cs220.submission.yamltests.load
import java.nio.file.Files
import TestBoilerplate._
import org.yaml.snakeyaml.error.YAMLException

class YamlTestsParserSuite extends FunSuite {

  forallFiles("./src/test/files/YamlTestsParserSuite/good") { path =>

    test(s"$path parses") {
      load(new String(Files.readAllBytes(path)))
    }

  }

  forallFiles("./src/test/files/YamlTestsParserSuite/bad") { path =>

    test(s"$path should not parse") {
      intercept[YAMLException] {
        load(new String(Files.readAllBytes(path)))
      }
    }

  }

}