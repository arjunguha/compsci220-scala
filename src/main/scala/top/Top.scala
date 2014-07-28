package cs220.submission.top

import cs220.submission._
import cs220.submission.yamlassignment.YamlAssignment
import cs220.submission.yamltests
import com.typesafe.config.ConfigFactory
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.{Paths, Files, Path}
import java.io.File
import scala.async.Async.{async, await}
import cs220.submission.sandbox._

class Top(confFile : String) {

  private val settings = TopSettings(Paths.get(confFile))
  private val testRunner = new TestRunner(settings)

  def assignmentDir(asgn : String, step : String) : Path = {
    settings.assignmentDir.resolve(asgn).resolve(step)
  }

  def getAssignment(asgn : String, step : String) : Assignment = {
    YamlAssignment(assignmentDir(asgn, step))
  }

  def getTestSuite(asgn : String, step : String) : List[Test] = {
    yamltests.load(assignmentDir(asgn, step).toString)
  }

  def checkSubmission(asgn : String, step : String, dir : Path)
    (implicit ec : ExecutionContext) : Future[List[SandboxResult]] = {

    val assignment = getAssignment(asgn, step)
    val tests = getTestSuite(asgn, step)

    // TODO(arjun): separate validation step

    Future.sequence(tests.map { test =>
      testRunner.runTest(assignment, test, dir)
    })

  }

}