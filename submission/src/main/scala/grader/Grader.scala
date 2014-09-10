package cs220.grader

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
import org.apache.commons.io.FileUtils
import spray.json._
import scala.util.{Try, Success, Failure}
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._

class Grader(confFile : String) {

  val top = new cs220.submission.top.Top(confFile)

  private val settings = cs220.submission.top.TopSettings(Paths.get(confFile))

  val moodleSubmission = """^(?:[^_]*)_(\d+)_.*$""".r

  private def extractSubmission(asgn: Assignment,
                                src: Path,
                                dst: Path): Unit = {
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

    val text = new String(Files.readAllBytes(dst.resolve(".metadata")))
    text.parseJson.asJsObject.getFields("assignment", "step") match {
      case Seq(JsString(a), JsString(s)) => {
        if (a == asgn.name && s == asgn.step) {
          // Ready!
          return
        }
        else {
          println(ansi().fg(YELLOW).a(s"$src/.metadata created with wrong arguments (ignoring)").newline().reset())
          FileUtils.deleteDirectory(dst.toFile)
          return
        }
      }
      case _ => {
        println(ansi().fg(YELLOW).a(s"$src/.metadata cannot be parsed (ignoring").newline().reset())
        FileUtils.deleteDirectory(dst.toFile)
        return
      }
    }

  }

  /**
   *
   * @param submissionsZip path to the submissions .zip file (from Moodle)
   * @param destination path to extract to
   * @param assignment name of the assignment
   * @param step assignment step
   */
  def extractSubmissions(assignment: String,
                         step: String,
                         submissionsZip: String,
                         destination: String): Unit = {
    import scala.sys.process._
    import scala.collection.JavaConversions._

    val asgn = top.getAssignment(assignment, step)

    val destDir = Paths.get(destination)

    if (!Files.isDirectory(destDir)) {
      println(ansi().fg(RED).a(s"${destination} must be a directory")
                    .newline().reset())
      return
    }
    val tmpDir = Files.createTempDirectory(settings.tmpDir, "extract")
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
            extractSubmission(asgn, submission, destDir.resolve(spireID))
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

}

object Grader {

  def apply(): Grader =
    new Grader("/home/vagrant/src/website/hw/_grading/sanity/settings.conf")

}