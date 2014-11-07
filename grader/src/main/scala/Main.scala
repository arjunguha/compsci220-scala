package grader

import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.io.File
import org.apache.commons.io.FileUtils
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import scala.concurrent.ExecutionContext.Implicits.global
import java.lang.ProcessBuilder
import ProcessTimer._

sealed trait GraderResult
case object Graded extends GraderResult
case object Ignored extends GraderResult
case class GradingError(msg: String) extends GraderResult

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

  def timeString(): String = {
    import java.util.Calendar
    import Calendar._
    val cal = Calendar.getInstance()
    s"${cal.get(HOUR)}:${cal.get(MINUTE)}:${cal.get(SECOND)}"
  }

  // i.e. grades a simple .scala file
  def gradeScalaScript(solution: Path, appendTestSuite: Path): GraderResult = {

    val base = solution.getParent

    println(s"${timeString} Grading $base ...")

    val target = base.resolve("gradingHacks.scala")
    Files.deleteIfExists(target)
    Files.copy(solution, target)
    Files.write(target, "\n\n\n".getBytes, APPEND)
    Files.write(target, Files.readAllBytes(appendTestSuite), APPEND)

    val cmpsci220Jar = "/home/vagrant/src/support-code/target/scala-2.11/cmpsci220.jar"
    val graderDeps = new String(Files.readAllBytes(Paths.get("/home/vagrant/src/grader/classpath")))
    val graderClasses = "/home/vagrant/src/grader/target/scala-2.11/classes"

    Files.deleteIfExists(base.resolve("stdout.txt"))
    val pb = new ProcessBuilder(
      "/usr/bin/timeout", "-s", "9", "5m",
      "/usr/bin/scala",
      "-classpath", s"$cmpsci220Jar:$graderDeps:$graderClasses",
      "-J-Xmx512m", "-J-Xss100m",
      "gradingHacks.scala")
    pb.redirectOutput(base.resolve("stdout.txt").toFile)
    pb.directory(base.toFile)

    // Print stdout in red
    print(ansi().fg(RED))
    val code = pb.start.waitFor
    print(ansi().reset())

    code match {
      case (137 | 124) => GradingError(s"Failed grading $base (hard timeout)")
      case 0 => Graded
      case n => GradingError(s"Failed grading $base (exit code $n)")
    }
  }

  def gradeAllScripts(solution: String, appendTestSuite: Path): Unit = {
    def grader(p: Path): GraderResult = {
      gradeScalaScript(p.resolve(solution), appendTestSuite)
    }

    gradeAll(grader)
  }

  def sbtGrader(p: Path): GraderResult = {
    println(s"${timeString} Grading $p ...")
    Files.copy(Paths.get("build.sbt"), p.resolve("build.sbt"),
               REPLACE_EXISTING)
    Files.copy(Paths.get("GradingMain.scala"), p.resolve("GradingMain.scala"),
               REPLACE_EXISTING)
    val stdout = p.resolve("stdout.txt")
    Files.deleteIfExists(stdout)
    val pb = new ProcessBuilder("/usr/bin/sbt", "runMain GradingMain")
    pb.redirectOutput(stdout.toFile)
    pb.directory(p.toFile)
    // Print stdout in red
    print(ansi().fg(RED))
    val code = pb.start.waitFor
    print(ansi().reset())

    code match {
      case (137 | 124) => GradingError(s"Failed grading $p (hard timeout)")
      case 0 => Graded
      case n => GradingError(s"Failed grading $p (exit code $n)")
    }
  }

  def gradeAllSbt(): Unit = {
    gradeAll(sbtGrader)
  }

  // Color output, .graded file management, etc.
  def gradeAll(grader: Path => GraderResult): Unit = {
    import scala.collection.JavaConversions._

    for (path <- Files.newDirectoryStream(Paths.get(""));
         if Files.isDirectory(path)) {
      if (Files.isRegularFile(path.resolve(".graded"))) {
        println(ansi().fg(YELLOW).a(s"Already graded $path").reset)
      }
      else if (Files.isRegularFile(path.resolve("compile-error"))) {
        println(ansi().fg(RED).a(s"Skipping $path due to earlier compile error").reset)
      }
      else {
        grader(path) match {
          case Ignored => {
            println(ansi().fg(YELLOW).a(s"Ignoring $path").reset)
          }
          case Graded => {
            Files.write(path.resolve(".graded"), Array[Byte]())
          }
          case GradingError(msg) => {
            println(ansi().fg(RED).a(s"Error grading $path ($msg)").reset)
          }
        }
      }
    }
  }

  args.toSeq match {
    case Seq("extract", srcZip, dstDir) => extractSubmissions(srcZip, dstDir)
    case Seq("check-graded") => Grading.checkGraded("")
    case Seq("fill-gradesheet", src, dst) => Grading.fillWorksheet(src, dst)
    case Seq("grade-all-sbt") => gradeAllSbt()
    case Seq("grade-all-scripts", solution, tests) =>
      gradeAllScripts(solution, Paths.get(tests))
    case Seq("grade-single-script", solution, tests) =>
      gradeScalaScript(Paths.get(solution), Paths.get(tests))
    case Seq("handback", smtp, subject) =>
      Handback(smtp, "sheet.csv", subject, "message.txt")
    case Seq("prepare-for-feedback", groups, perGroup, src, dst, dirs@_*) =>
      Feedback.prepareForFeedback(groups.toInt, perGroup.toInt, src, dst, dirs)
    case _ => {
      println("Invalid arguments. Read source code for help.")
      println(args.toList)
    }
  }
  System.exit(0)
}
