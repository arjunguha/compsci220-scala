package cs220.submission.assignments

import cs220.submission.messages._
import org.scalatest.FunSuite
import java.nio.file.{Paths, Files}

class AssignmentConfigSuite extends FunSuite {

  import scala.collection.JavaConversions._
  val goodTests = "./src/test/files/good-tests"
  val badTests = "./src/test/files/bad-tests"

  Files.newDirectoryStream(Paths.get(goodTests)).foreach { dir =>
    test(s"$dir should load successfully") {
      assert(AssignmentConfig(dir.resolve("assignment.conf")) != null)
    }
  }

  Files.newDirectoryStream(Paths.get(badTests)).foreach { dir =>
    test(s"$dir should fail to load") {
      // TODO(arjun): more precise exception
      intercept[Throwable] {
        val _ = AssignmentConfig(dir.resolve("assignment.conf"))
      }
    }
  }

}
