package cmpsci220.plugin

object Plugin extends sbt.AutoPlugin {
  import org.apache.commons.io.IOUtils
  import java.io.File
  import sbt._
  import Keys._
  import FileUtils._
  import spray.json._
  import java.nio.file._

  override def trigger = allRequirements


  object autoImport {
    lazy val submit = TaskKey[Unit]("submit")
    lazy val checkstyle = TaskKey[Unit]("checkstyle")
    lazy val findMisplacedFiles = TaskKey[Unit]("find-misplaced-files")
    lazy val directoryWarnings = TaskKey[Unit]("directory-warnings")
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

  override def projectSettings = super.projectSettings ++
    Seq(org.scalastyle.sbt.ScalastylePlugin.projectSettings :_*) ++
    Seq(
      scalaVersion := "2.11.8",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-Xfatal-warnings"
      ),
      resolvers += "PLASMA" at "https://dl.bintray.com/plasma-umass/maven",
      parallelExecution in Test := false,
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      libraryDependencies += "edu.umass.cs" %% "compsci220" % "1.3.1",
      findMisplacedFiles := Tasks.findMisplacedFiles(streams.value.log),
      directoryWarnings := Tasks.directoryWarnings(streams.value.log),
      checkstyle := Tasks.checkstyle(
        streams.value,
        target.value,
        org.scalastyle.sbt.ScalastylePlugin.scalastyleTarget.value),
      compile := {
        val result = (compile in Compile).value
        checkstyle.value
        result
      },
      compile <<= compile.dependsOn(findMisplacedFiles, directoryWarnings),
      submit := {
        submitTask
      },
      test := {
        val result = (test in Test).value
        checkstyle.value
        result
      },
      submit <<= submit.dependsOn(compile)
    )
}
