package cs220.submission.top

import cs220.submission.{TestResult, ExpectedException}
import java.nio.file.{Paths, Files}
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}
import rx.lang.scala._

object Main extends App {

  AnsiConsole.systemInstall()

  val logger = org.slf4j.LoggerFactory.getLogger("Main")

  logger.info("Started application")

  import ExecutionContext.Implicits.global


  val topSettingsPath = System.getenv("ASSIGNMENTS_PATH")
  if (topSettingsPath == null) {
     print(ansi.fg(RED).a("ASSIGNMENTS_PATH is not set. Contact staff for help.")
                 .newline().reset())

     System.exit(1)
  }
  val top = new Top(topSettingsPath)


  val done = Promise[Unit]()

  def report  = Observer[TestResult](
    (result : TestResult) => print(result.describe),
    (exn : Throwable) => print(ansi.fg(RED).a(exn).newline().reset()),
    () => { done.success(()); () })

  args match {
    case Array("check-submission", asgn, step, dir) => {
      println("Checking ...")
      top.checkSubmission(asgn, step, Paths.get(dir)).subscribe(report)
      Await.result(done.future, Duration.Inf)
    }
    case Array("tar", asgn, step) => {
      val filename = s"solution-${asgn}-${step}.tgz"
      top.zipSubmission(asgn, step, Paths.get("."), Paths.get(filename))
      println(s"Submit the file $filename to Moodle")
    }
    case Array("check-assignment", asgn, step) => {
      Await.result(top.checkAssignment(asgn, step), Duration.Inf)
      println(s"$asgn $step is OK.")
    }
    case _ => {
      print(ansi.fg(RED).a("Invalid command-line arguments.").reset().newline())
    }
  }

  System.exit(0)



}