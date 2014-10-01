package grader

import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardOpenOption.{APPEND, CREATE}
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._

class MyProcessLogger(stdout: Path) extends scala.sys.process.ProcessLogger {

  def buffer[T](f: => T): T = f

  def err(s: => String): Unit = {
    println(ansi().fg(RED).a(s).reset)
  }

  def out(s: => String): Unit = {
    Files.write(stdout, s.getBytes, APPEND, CREATE)
    Files.write(stdout, "\n".getBytes, APPEND)
  }

}