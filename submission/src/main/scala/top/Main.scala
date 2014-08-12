package cs220.submission.top

import cs220.submission.{TestResult, ExpectedException}
import java.nio.file.{Paths, Files}
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}

object Main extends App {

  val logger = org.slf4j.LoggerFactory.getLogger("Main")

  logger.info("Started application")

  import ExecutionContext.Implicits.global

  AnsiConsole.systemInstall()

  val topSettingsPath = System.getenv("ASSIGNMENTS_PATH")
  if (topSettingsPath == null) {
     print(ansi.fg(RED).a("ASSIGNMENTS_PATH is not set. Contact staff for help.")
                 .newline().reset())

     System.exit(1)
  }
  val top = new Top(topSettingsPath)

  def report : PartialFunction[Try[List[TestResult]], Unit] = {
    case Success(results) => {
      for (result <- results) {
        print(result.describe)
      }
    }
    case Failure(exn : ExpectedException) => {
      print(ansi.fg(RED).a(exn.getMessage).newline().reset())
    }
    case Failure(exn) => {
      print(ansi.fg(RED).a("An unexpected error occurred. Please report.").reset())
    }
  }

  args match {
    case Array("check-submission", asgn, step, dir) => {
      val fut = top.checkSubmission(asgn, step, Paths.get(dir)) andThen report
      Await.result(fut recover { case _ => () } , Duration.Inf)
    }
    case Array("check-assignment", asgn, step) => {
      Await.result(top.checkAssignment(asgn, step), Duration.Inf)
      println(s"$asgn $step is OK.")
    }
    case _ => {
      print(ansi.fg(RED).a("Invalid command-line arguments.").newline().reset())
    }
  }

  System.exit(0)

}