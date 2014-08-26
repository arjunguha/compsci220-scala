package cs220.submission.top

import cs220.submission._
import cs220.submission.yamlassignment.YamlAssignment
import cs220.submission.yamltests.YamlTest
import com.typesafe.config.ConfigFactory
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.{Paths, Files, Path}
import java.io.File
import scala.async.Async.{async, await}
import rx.lang.scala._

private object Top {
  val log = org.slf4j.LoggerFactory.getLogger(classOf[Top])
}

class Top(confFile : String) {

  private val settings = TopSettings(Paths.get(confFile))
  private val testRunner = new TestRunner(settings)

  def assignmentDir(asgn : String, step : String) : Path = {
    settings.assignmentDir.resolve(asgn).resolve(step)
  }

  def getAssignment(asgn : String, step : String) : Assignment = {
    val dir = assignmentDir(asgn, step)
    val yamlFile = dir.resolve("assignment.yaml")
    if (Files.isRegularFile(yamlFile)) {
      YamlAssignment(asgn, step, dir.resolve("assignment.yaml"), dir)
    }
    else {
      throw new InvalidAssignment(s"$asgn $step is not a valid step")
    }
  }

  def getTestSuite(asgn : String, step : String) : List[Test] = {
    val path = assignmentDir(asgn, step).resolve("tests.yaml")
    YamlTest(new String(Files.readAllBytes(path)))
  }

  def checkAssignment(asgn : String, step : String)
    (implicit ec : ExecutionContext) : Future[Unit] = async {
    val assignment = getAssignment(asgn, step)
    val tests = getTestSuite(asgn, step)
    ()
  }


  def checkSubmission(asgn : String, step : String, dir : Path)
    (implicit ec : ExecutionContext) : Future[List[TestResult]] = async {
    val assignment = getAssignment(asgn, step)
    val tests = getTestSuite(asgn, step)
    await(tests.foldRight(Future{List[TestResult]()}) { (test, fut) => async {
        val tl = await(fut)
        val hd = await(testRunner.runTest(assignment, test, dir))
        Top.log.info(hd.describe().toString)
        hd :: tl

      }
    })
  }


  def checkSubmissionO(asgn : String, step : String, dir : Path)
    (implicit ec : ExecutionContext) : Observable[TestResult] = {
    val assignment = getAssignment(asgn, step)

    val tests = Observable.from(getTestSuite(asgn, step))

    tests.concatMap({ test =>
      Observable.defer(Observable.from(testRunner.runTest(assignment, test, dir))) })
  }

}