package cmpsci220.plugin

import sbt._
import Keys._
import FileUtils._
import spray.json._

object Plugin extends sbt.AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    lazy val submit = TaskKey[Unit]("submit")
    lazy val submitTests = TaskKey[Unit]("submit-tests")
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

  def submitTestsTask(): Unit = {

    val files = findFiles("src/test/scala")(fileNameHasSuffix(_, ".scala"))
    val tgz = TgzBuilder("test-suite.tar.gz")
    for (file <- files) {
      tgz.add(file)
    }

    tgz.close()

    println("Created test-suite.tar.gz. Upload this file to Captain Teach.")
  }

  override def projectSettings = super.projectSettings ++ Seq(
    submit := {
      (test in Test).value
      submitTask
    },
    submitTests := {
      submitTestsTask
    }
  )
}