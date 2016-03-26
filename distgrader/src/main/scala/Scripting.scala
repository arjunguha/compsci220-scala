package grading

import java.util.zip.ZipOutputStream

import akka.actor.ActorLogging
import akka.util.Timeout
import grading.Messages.ContainerExit
import org.apache.commons.io.{FileUtils, IOUtils}
import edu.umass.cs.zip._
import scala.concurrent.{ExecutionContext, Future}

object Scripting {

  import java.nio.file.{Paths, Files, Path, FileSystems}
  import scala.util.{Try, Success, Failure}
  import scala.collection.JavaConversions._
  import Messages._

  // Matches submission ID
  private val moodleSubmissionRegex = """^(?:[^_]*)_(\d+)_.*$""".r

  def extract(src: String, dst: String) = {
    assert(Files.isDirectory(Paths.get(dst)), s"$dst must be a directory")
    assert(Files.isRegularFile(Paths.get(src)), s"$src must be a .zip file")

    val zip = FileSystems.newFileSystem(Paths.get(src),  null)
    val files = Files.newDirectoryStream(zip.getPath("/")).toList
    for (submission <- files) {
      val filename = submission.getFileName().toString
      moodleSubmissionRegex.findFirstIn(filename) match {
        case Some(moodleSubmissionRegex(spireID)) => {
          val dir = Paths.get(dst).resolve(spireID)
          if (Files.isDirectory(dir) &&
              Files.getLastModifiedTime(dir).compareTo(Files.getLastModifiedTime(submission)) < 0) {
            println(s"$dir has been updated on Moodle. Delete the directory to overwrite it.")
          }
          if (!Files.isDirectory(dir)) {
            // Pull it out of the zip file
            val tgz = Paths.get(dst).resolve(filename)
            Files.write(tgz, Files.readAllBytes(submission))
            Files.createDirectory(dir)
            import scala.sys.process._
            if (Seq("/usr/bin/tar", "-xzf", tgz.toString, "--directory", dir.toString).! != 0) {
              println(s"Error extracting $filename")
              FileUtils.deleteDirectory(dir.toFile)
            }
            else if (Files.isRegularFile(dir.resolve(".metadata")) == false) {
              println(s"No .metadata for $filename")
              FileUtils.deleteDirectory(dir.toFile)
            }
            else {
              Files.setLastModifiedTime(dir, Files.getLastModifiedTime(submission))
            }
            Files.delete(tgz)
          }
        }
        case None => ()
      }
    }
  }

  def assignments(src: String): List[Path] = {
    Files.newDirectoryStream(Paths.get(src)).toList
      .filter(p => Files.isDirectory(p))
  }

  def readRubric(path: Path): Rubric = {
    import MyJsonProtocol._
    import spray.json._

    assert(!Files.exists(path) || Files.isRegularFile(path),
      s"$path is a directory or special file")
    if (!Files.exists(path)) {
      Rubric(Map())
    }
    else {
      Try(new String(Files.readAllBytes(path)).parseJson.convertTo[Rubric]) match {
        case Success(json) => json
        case Failure(exn) => {
          println(s"Error parsing $path")
          throw exn
        }
      }
    }
  }

  def updateState(file: Path)(body: Rubric => Future[Rubric])(implicit ec: ExecutionContext) = {
    import MyJsonProtocol._
    import spray.json._
    val oldState = Scripting.readRubric(file)

    body(oldState).map(newState => {
      Files.write(file, newState.toJson.prettyPrint.getBytes)
      Files.write(file.getParent.resolve("report.txt"), newState.toString.getBytes)
    })
  }

}

class Scripting(ip: String) {

  import java.nio.file.{Path}
  import akka.actor.{Props, ActorSystem, Actor}
  import scala.concurrent._
  import scala.concurrent.duration._
  import Messages._
  import scala.collection.JavaConversions._

  val system = ActorSystem("controller", AkkaInit.remotingConfig(ip, 5000))
  private val controllerActor = system.actorOf(Props[ControllerActor], name="controller")

  def run(timeout: Int, command: Seq[String], zip: Array[Byte], label: String = "No label"): Future[ContainerExit] = {
    val p = Promise[ContainerExit]()
    val run = Run("gcr.io/umass-cmpsci220/student", timeout, "/home/student/hw", command, Map("/home/student/hw" -> zip))
    controllerActor ! (label, p, run)
    p.future
  }

  implicit class RichPath(p: Path) {

    def filename(): String = p.getFileName.toString

  }

}
