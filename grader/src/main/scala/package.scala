import java.nio.file.{Paths, Files, Path}
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.Ansi.Color.{RED, YELLOW}

package object grader {

  def timeString(): String = {
    import java.util.Calendar
    import Calendar._
    val cal = Calendar.getInstance()
    s"${cal.get(HOUR)}:${cal.get(MINUTE)}:${cal.get(SECOND)}"
  }


  def submissions(moodleCsv: String = "sheet.csv"): List[Path] = {
    val moodle = MoodleSheet(moodleCsv)
    val r = moodle.submissionIds.map { submitId =>
      val p = Paths.get(submitId)
      if (Files.isDirectory(p)) {
        Some(p)
      }
      else {
        val email = moodle.emailById(submitId).get // should not fail
        println(ansi().fg(YELLOW).a(s"No submission from $submitId <$email>")
                      .reset)
        None
      }
    }
    r.filterNot(_.isEmpty).map(_.get).toList
  }

  def hadCompileError(path: Path): Boolean = {
    Files.isRegularFile(path.resolve("compile-error"))
  }





}