package cs220.submission

import cs220.submission.sandbox.{SandboxResult, Complete, DidNotFinish}
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Attribute._
import org.fusesource.jansi.Ansi.Color._

case class TestResult(test : Test, result : SandboxResult) {

  def describe() : Ansi = result match {
    case Complete(0, _, _) => {
      ansi().fg(GREEN).a(s"- ${test.description}\n").reset()
    }
    case Complete(code, stdout, stderr) => {
      ansi().fg(RED).a(s"- ${test.description}\n").reset()
        .a(INTENSITY_FAINT).a(stdout).newline().reset()
        .a(INTENSITY_FAINT).a(stderr).newline().reset()
    }
    case DidNotFinish(_, _) => {
      ansi().fg(RED).a(s"- ${test.description}\n").reset()
        .a("Did not finish (time limit: ${test.timeLimit.toSeconds} seconds)\n")
    }
  }


}
