package cmpsci220.plugin
import sbt._

object Plugin extends AutoPlugin {
  import org.apache.commons.io.IOUtils
  import java.io.File
  import sbt._
  import Keys._
  import FileUtils._
  import java.nio.file._

  override def trigger = allRequirements
  override def requires = plugins.JvmPlugin

  object autoImport {
    val submit = taskKey[Unit]("submit")
    val checkstyle = taskKey[Unit]("checkstyle")
    val findMisplacedFiles = taskKey[Unit]("find-misplaced-files")
    val directoryWarnings = taskKey[Unit]("directory-warnings")
  }

  import autoImport._

  lazy val checkstyleTask = Def.task {
    Tasks.checkstyle(
      streams.value,
      target.value,
      org.scalastyle.sbt.ScalastylePlugin.autoImport.scalastyleTarget.value)
  }

  lazy val submitTask = Def.task {
    Tasks.submitTask()
  }

  lazy val findMisplacedFilesTask = Def.task {
    Tasks.findMisplacedFiles(streams.value.log)
  }

  lazy val directoryWarningsTask = Def.task {
    Tasks.directoryWarnings(streams.value.log)
  }

  override def projectSettings = super.projectSettings ++
    Seq(org.scalastyle.sbt.ScalastylePlugin.projectSettings :_*) ++
    Seq(
      scalaVersion := "2.12.4",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-Xfatal-warnings"
      ),
      resolvers += "PLASMA" at "https://dl.bintray.com/plasma-umass/maven",
      parallelExecution in Test := false,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test",
      libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
      findMisplacedFiles := findMisplacedFilesTask.value,
      directoryWarnings := directoryWarningsTask.value,
      // Run ScalaStyle after compilation succeeds. ScalaStyle displays
      // terrible error messages when files have syntax errors. By running
      // it after compilation, we get better messages from scalac. However,
      // this means that the program builds successfully even if it doesn't
      // pass the style checker.
      checkstyle := checkstyleTask.triggeredBy(compile in Compile).value,
      // Print warnings about misplaced files and non-existent directories
      // before compilation.
      (compile in Compile) := (compile in Compile)
        .dependsOn(findMisplacedFiles, directoryWarnings).value,
      // We refuse to create a submittable .tgz if the program doesn't
      // compile and pass ScalaStyle.
      submit := submitTask.dependsOn(compile in Compile, checkstyle).value
    )
}
