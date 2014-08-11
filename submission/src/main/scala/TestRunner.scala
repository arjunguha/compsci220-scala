package cs220.submission

import cs220.submission.sandbox._
import scala.util.{Try, Success, Failure}
import java.nio.file.{Files, Path}
import scala.concurrent._
import scala.async.Async.{async, await}
import plasma.docker._
import org.apache.commons.io.FileUtils

class TestRunner(settings : cs220.submission.top.TopSettings) {

  def runTest(asgn : Assignment, test : Test, submitDir : Path)
    (implicit ec : ExecutionContext) : Future[SandboxResult] = {
    val workDir = Files.createTempDirectory(settings.tmpDir, "TestRunner")
    asgn.prepareSubmission(submitDir, workDir)
    test.prepare(workDir)
    val sandbox = new Sandbox(settings.dockerUrl)
    sandbox(workDir, "/data", test.image, test.command,
            test.memoryLimitBytes, test.timeLimit) andThen {
      case _ => FileUtils.deleteDirectory(workDir.toFile)
    }
  }

}