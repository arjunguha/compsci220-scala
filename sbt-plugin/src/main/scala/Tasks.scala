package cmpsci220.plugin

object Tasks {

  import java.nio.file.{Files, Paths, Path}

  def directoryWarnings(): Unit = {

    val main = Paths.get("").resolve("src").resolve("main").resolve("scala")
    val test = Paths.get("").resolve("src").resolve("test").resolve("scala")

    if (!Files.isDirectory(main)) {
      println(s"WARNING: the directory $main is missing: there is no code to run.")
    }

    if (!Files.isDirectory(test)) {
      println(s"WARNING: the directory $test is missing: there are no test cases.")
    }

  }

  def findMisplacedFiles(): Unit = {
    val scalaFiles = FileUtils.findFiles(Paths.get("")) { path =>
      path.getFileName.toString.endsWith(".scala")
    }

    val main = Paths.get("src", "main", "scala")
    val test = Paths.get("src", "test", "scala")

    val misplacedFiles = scalaFiles.filterNot { path =>
      path.startsWith(main) || path.startsWith(test)
    }

    if (!misplacedFiles.isEmpty) {
      println("WARNING: The following Scala files are going to be ignored:")
      for (path <- misplacedFiles) {
        println(s"  - $path")
      }
      println("All code must be in src/main/scala and all tests must be in src/test/scala.")
    }

  }

}