import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Attribute._
import org.fusesource.jansi.Ansi.Color._

package object cmpsci220 {

  AnsiConsole.systemInstall()

  /** This value enables all test cases */

  private lazy val testsEnabled = System.getenv("DISABLE_TESTS") match {
    case "TRUE" => false
    case "FALSE" => true
    case null => true
    case str =>
      sys.error(s"unexpected value for the DISABLE_TESTS envvar $str")
  }

  /** Signals an error
   *
   * @group Miscellaneous
   */
  val error = sys.error _

  /**
   * @group Testing
   */
  def test(description : String)(body : => Unit) : Unit = {
    if (testsEnabled) {
      try {
        body
        print(ansi().fg(GREEN).a(s"Succeeded $description").reset().newline())
      }
      catch {
        case (exn : Throwable) => {
          print(ansi().fg(RED).a(s"Failed $description").reset().newline()
                      .a(INTENSITY_FAINT).a(exn.toString).reset().newline())
        }
      }
    }
  }

  /**
   * @group Testing
   */
  def fails(description : String)(body : => Unit) : Unit = {
    if (testsEnabled) {
      try {
        body
        print(ansi().fg(RED).a(s"Failed $description").reset().newline())
      }
      catch {
        case (exn : Throwable) => {
          print(ansi().fg(GREEN).a(s"Succeeded $description").reset().newline()
                      .a(INTENSITY_FAINT).a(exn.toString).reset().newline())
        }
      }
    }
  }


}