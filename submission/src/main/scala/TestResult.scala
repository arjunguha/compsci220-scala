package plasma.grader

import plasma.grader.sandbox.{SandboxResult, Complete, DidNotFinish}
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Attribute._
import org.fusesource.jansi.Ansi.Color._

case class TestResult(test : Test, result : SandboxResult) {

  def describe() : Ansi = result match {
    case Complete(0, _, _) => {
      ansi().fg(GREEN).a(s"- (PASSED) ${test.description}").reset().newline()
    }
    case Complete(code, stdout, stderr) => {
      ansi().fg(RED).a(s"- (FAILED) ${test.description}").reset().newline()
        .a(INTENSITY_FAINT).a(stdout).reset().newline()
        .a(INTENSITY_FAINT).a(stderr).reset().newline()
    }
    case DidNotFinish(_, _) => {
      val t = test.timeLimit.toSeconds
      ansi().fg(RED).a(s"- (TIMEOUT) ${test.description}").reset().newline()
        .a(s"Did not finish (time limit: $t seconds)").reset().newline()
    }
  }


}
