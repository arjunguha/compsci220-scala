package cmpsci220.plugin

object Tasks {

  import java.nio.file.{Files, Paths, Path}
  import sbt.Logger
  import FileUtils._

  val scalastyleBytes = resourceToByteArray("scalastyle-config.xml")

  def directoryWarnings(log: Logger): Unit = {

    val main = Paths.get("").resolve("src").resolve("main").resolve("scala")
    val test = Paths.get("").resolve("src").resolve("test").resolve("scala")

    if (!Files.isDirectory(main)) {
      log.warn(s"The directory $main is missing: there is no code to run.")
    }

    if (!Files.isDirectory(test)) {
      log.warn(s"The directory $test is missing: there are no test cases.")
    }

  }

  def findMisplacedFiles(log: Logger): Unit = {
    val scalaFiles = FileUtils.findFiles(Paths.get("")) { path =>
      path.getFileName.toString.endsWith(".scala")
    }

    val main = Paths.get("src", "main", "scala")
    val test = Paths.get("src", "test", "scala")

    val misplacedFiles = scalaFiles.filterNot { path =>
      path.startsWith(main) || path.startsWith(test)
    }

    if (!misplacedFiles.isEmpty) {
      log.warn("The following Scala files are going to be ignored:")
      for (path <- misplacedFiles) {
        log.warn(s"  - $path")
      }
      log.warn("All code must be in src/main/scala and all tests must be in src/test/scala.")
    }
  }

  def checkstyle(streams: sbt.Keys.TaskStreams,
                 target: java.io.File,
                 scalastyleTarget: java.io.File): Unit = {
    import org.scalastyle.sbt.Tasks.doScalastyle
    val p = Files.createTempFile(Paths.get(""), "scalastyle-config-", ".xml")
    try {
      Files.write(p, scalastyleBytes)
      val sources = (Paths.get("src/main/scala") ::
        Paths.get("src/test/scala") ::
        listDir(Paths.get(""), "*.scala")).map(_.toFile)
      doScalastyle(
        args = Seq("q"),
        config = p.toFile,
        configUrl = None,
        failOnError = true,
        scalastyleSources = sources,
        scalastyleTarget = scalastyleTarget,
        streams = streams,
        refreshHours = 0,
        target = target,
        urlCacheFile = null)
    }
    finally {
      Files.deleteIfExists(p)
    }
  }

}