package plasma.grader

import plasma.grader.sandbox._
import scala.util.{Try, Success, Failure}
import java.nio.file.{Files, Path}
import scala.concurrent._
import scala.async.Async.{async, await}
import plasma.docker._
import org.apache.commons.io.FileUtils

class TestRunner(settings : plasma.grader.top.TopSettings) {

  def runTest(asgn : Assignment, test : Test, submitDir : Path)
    (implicit ec : ExecutionContext) : Future[TestResult] = {
    val workDir = Files.createTempDirectory(settings.tmpDir, "TestRunner")

    val result = async {
      asgn.prepareSubmission(submitDir, workDir)
      test.prepare(workDir, asgn)
      val sandbox = new Sandbox(settings.dockerUrl)
      val result = await(sandbox(workDir, "/data", test.image, test.command,
                                 test.memoryLimitBytes, test.timeLimit))
      TestResult(test, result)
    }
    result andThen {
      case _ => FileUtils.deleteDirectory(workDir.toFile)
    }
    result
  }

}