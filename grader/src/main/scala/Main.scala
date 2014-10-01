package grader

import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardOpenOption.APPEND
import java.io.File
import org.apache.commons.io.FileUtils
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import scala.concurrent.ExecutionContext.Implicits.global
import ProcessTimer._

object Main extends App {

  AnsiConsole.systemInstall()

  val moodleSubmission = """^(?:[^_]*)_(\d+)_.*$""".r

  private def extractSubmission(src: Path, dst: Path): Unit = {
    import scala.sys.process._

    if (Files.isDirectory(dst)) {
        println(ansi().fg(YELLOW).a(s"$dst exists; skipping")
                      .newline().reset())
      return
    }

    Files.createDirectory(dst)

    if (Seq("/bin/tar",
            "-xzf", src.toString,
            "--directory", dst.toString).! != 0) {
      println(ansi().fg(YELLOW).a(s"$src is not a .tgz (ignoring)").newline().reset())
      FileUtils.deleteDirectory(dst.toFile)
      return
    }

    if (!Files.isRegularFile(dst.resolve(".metadata"))) {
      println(ansi().fg(YELLOW).a(s"$src/.metadata does not exist (ignoring)")
                    .newline().reset())
      FileUtils.deleteDirectory(dst.toFile)
      return
    }

    // Ready to go!
  }

  /**
   *
   * @param submissionsZip path to the submissions .zip file (from Moodle)
   * @param destination path to extract to
   * @param assignment name of the assignment
   * @param step assignment step
   */
  def extractSubmissions(submissionsZip: String, destination: String): Unit = {
    import scala.sys.process._
    import scala.collection.JavaConversions._

    val destDir = Paths.get(destination)

    if (!Files.isDirectory(destDir)) {
      println(ansi().fg(RED).a(s"${destination} must be a directory")
                    .newline().reset())
      return
    }
    val tmpDir = Files.createTempDirectory(Paths.get("/tmp"), "extract")
    try {
      if (Seq("/usr/bin/unzip",
              "-d", tmpDir.toString,
              submissionsZip).! != 0) {
        println(ansi().fg(RED).a(s"Error unzipping $submissionsZip")
                      .newline().reset())
        return
      }

      for (submission <- Files.newDirectoryStream(tmpDir)) {
        val filename = submission.getFileName().toString
        moodleSubmission.findFirstIn(filename) match {
          case Some(moodleSubmission(spireID)) => {
            extractSubmission(submission, destDir.resolve(spireID))
          }
          case None => {
            println(ansi().fg(RED).a(s"Could not parse filename: $filename")
                          .newline().reset())
          }
        }
      }
    }
    finally {
      FileUtils.deleteDirectory(tmpDir.toFile)

    }
  }

  // i.e. grades a simple .scala file
  def gradeScalaScript(solution: Path, appendTestSuite: Path): Unit = {
    import scala.sys.process._

    val base = solution.getParent

    if (Files.isRegularFile(base.resolve(".graded"))) {
      println(ansi().fg(YELLOW).a(s"Skipping $base (found .graded)").reset)
      return
    }

    println(s"Grading $base ...")

    val target = base.resolve("gradingHacks.scala")
    Files.deleteIfExists(target)
    Files.copy(solution, target)
    Files.write(target, "\n\n\n".getBytes, APPEND)
    Files.write(target, Files.readAllBytes(appendTestSuite), APPEND)

    val cmpsci220Jar = "/home/vagrant/src/support-code/target/scala-2.11/cmpsci220.jar"
    val graderJar = "/home/vagrant/src/grader/target/scala-2.11/grader.jar"

    val cmd = Seq("/usr/bin/timeout", "-s", "9", "90s",
                  "/usr/bin/scala",
                  "-classpath", s"$cmpsci220Jar:$graderJar",
                  "-J-Xmx512m", "-J-Xss100m",
                  target.toString)

    Files.deleteIfExists(base.resolve("stdout.txt"))
    val process = cmd.run(new MyProcessLogger(base.resolve("stdout.txt")))

    process.exitValue match {
      case 124 => {
        println(ansi().fg(YELLOW).a(s"Failed grading $base (timeout)").reset)
      }
      case 0 => {
        Files.write(base.resolve(".graded"), "graded".getBytes)
      }
      case _ => {
        println(ansi().fg(RED).a(s"Failed grading $base (non-zero exit code)").reset)
      }
    }

    // Files.deleteIfExists(base.resolve("grading-stdout.txt"))
    // val scriptFile = base.resolve("gradeMe.sh")
    // Files.deleteIfExists(scriptFile)
    // Files.write(scriptFile, "cd `dirname $0`; scala -classpath /home/vagrant/src/support-code/target/scala-2.11/cmpsci220.jar:/home/vagrant/src/grader/target/scala-2.11/grader.jar gradingHacks.scala > grading-stdout.txt\n".getBytes)
    // val cmd = Seq("/bin/bash", scriptFile.toString)
    // if (cmd.! == 0) {
    //   Files.write(base.resolve(".graded"), "graded".getBytes)
    // }
  }

  def gradeAllScripts(solution: String, appendTestSuite: Path): Unit = {
    import scala.collection.JavaConversions._

    for (path <- Files.newDirectoryStream(Paths.get(""))) {
      val sol = path.resolve(solution)
      if (Files.isDirectory(path) && Files.isRegularFile(sol)) {
        gradeScalaScript(sol, appendTestSuite)
      }
      else {
        println(ansi().fg(YELLOW).a(s"Skipping $path").reset)
      }
    }
  }

  args match {
    case Array("extract", srcZip, dstDir) => extractSubmissions(srcZip, dstDir)
    case Array("grade-all-scripts", solution, tests) =>
      gradeAllScripts(solution, Paths.get(tests))
    case Array("grade-single-script", solution, tests) =>
      gradeScalaScript(Paths.get(solution), Paths.get(tests))
    case _ => println("Invalid arguments. Read source code for help.")
  }
  System.exit(0)
}
