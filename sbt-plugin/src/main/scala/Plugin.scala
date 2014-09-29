package cmpsci220.plugin

import sbt._
import Keys._
import FileUtils._
import spray.json._

object Plugin extends sbt.AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    lazy val submit = TaskKey[Unit]("submit")
  }

  import autoImport._

  def submitTask(): Unit = {

    val files = findFiles("")(fileNameHasSuffix(_, ".scala"))
    val tgz = TgzBuilder("submission.tar.gz")
    for (file <- files) {
      tgz.add(file)
    }

    val metadata = JsObject("time" -> JsNumber(System.currentTimeMillis))
    tgz.write(".metadata", metadata.compactPrint.getBytes)

    tgz.close()

    println("Created submission.tar.gz. Upload this file to Moodle.")
  }

  override def projectSettings = super.projectSettings ++ Seq(
    submit := {
      (test in Test).value
      submitTask
    }
  )
}