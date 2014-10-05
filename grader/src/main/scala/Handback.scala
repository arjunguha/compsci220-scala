package grader

import io.github.arjunguha.mail._
import java.nio.file.{Paths, Files, Path}
import com.typesafe.config.ConfigFactory

class Handback(smtpAuth: SMTPAuth,
               sheet: MoodleSheet,
               subject: String,
               messagePath: Path,
               root: Path) {

  val body = new String(Files.readAllBytes(messagePath))

  def handbackDirectory(id: String): Unit = {
    import scala.sys.process._

    val src = root.resolve(id)
    if (Files.isRegularFile(src.resolve("emailed-back"))) {
      println(s"Ignoring $id (emailed back)")
      return
    }

    val maybeEmail = sheet.emailById(id)
    if (maybeEmail.isEmpty) {
      println(s"No email found for $id. Ignoring.")
      return
    }

    val dst = root.resolve(s"${id}.tar.gz")
    val email = maybeEmail.get // should never fail due to test above

    try {
      val code = Seq("/bin/tar",  "czf", dst.toString, src.toString).!
      assert(code == 0)

      val attach = Attachment(path = dst.toString, name = s"${id}.tar.gz")
      val msg = Message(from = "Arjun Guha <arjun@cs.umass.edu>",
                        to = email,
                        subj = subject,
                        msg = body,
                        attachments = List(attach),
                        auth = smtpAuth)
      msg.send()
      Files.write(src.resolve("emailed-back"), Array[Byte]())
    }
    finally {
      Files.deleteIfExists(dst)
    }
  }

  def handbackAll(): Unit = {
    import scala.collection.JavaConversions._

    for (path <- Files.newDirectoryStream(root)
                   .filter(p => Files.isDirectory(p))) {
      val id = path.getFileName.toString
      handbackDirectory(id)
    }

  }

}

object Handback {

  def apply(smtp: String, sheet: String, subject: String, message: String) = {
    val obj = new Handback(
      smtpAuth = SMTPAuth(ConfigFactory.parseFile(Paths.get(smtp).toFile)),
      sheet = MoodleSheet(sheet),
      subject = subject,
      messagePath = Paths.get(message),
      root = Paths.get(""))
    obj.handbackAll()
  }
}