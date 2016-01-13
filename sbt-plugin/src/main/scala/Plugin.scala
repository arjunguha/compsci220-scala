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

  Tasks.directoryWarnings()

  object autoImport {
    lazy val submit = TaskKey[Unit]("submit")
    lazy val submitTests = TaskKey[Unit]("submit-tests")
    lazy val checkstyle = TaskKey[Unit]("checkstyle")
    lazy val findMisplacedFiles = TaskKey[Unit]("find-misplaced-files")
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

  val scalastyleBytes = resourceToByteArray("scalastyle-config.xml")

  override def projectSettings = super.projectSettings ++
    Seq(org.scalastyle.sbt.ScalastylePlugin.projectSettings :_*) ++
    Seq(
      scalaVersion := "2.11.7",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-Xfatal-warnings"
      ),
      findMisplacedFiles := Tasks.findMisplacedFiles,
      checkstyle := {
        import org.scalastyle.sbt.Tasks.doScalastyle
        import org.scalastyle.sbt.ScalastylePlugin
        val p = Files.createTempFile(Paths.get(""), "scalastyle-config-", ".xml")
        Files.write(p, scalastyleBytes)
        val sources = (Paths.get("src/main/scala") ::
          Paths.get("src/test/scala") ::
          listDir(Paths.get(""), "*.scala")).map(_.toFile)
        doScalastyle(
          args = Seq(),
          config = p.toFile,
          configUrl = None,
          failOnError = true,
          scalastyleSources = sources,
          scalastyleTarget = ScalastylePlugin.scalastyleTarget.value,
          streams = streams.value,
          refreshHours = 0,
          target = target.value,
          urlCacheFile = null)
        Files.deleteIfExists(p)
      },
      submit := {
        (test in Test).value
        submitTask
      },
      submitTests := {
        submitTestsTask
      },
      compile <<= (compile in Compile).dependsOn(checkstyle, findMisplacedFiles)
    )
}