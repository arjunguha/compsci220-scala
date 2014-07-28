package cs220.submission.yamltests

import java.nio.file.{Path, Files}
import cs220.submission.Test
import scala.concurrent.duration.Duration

private[yamltests] class YamlTest(
  val description : String,
  val points : Int,
  val memoryLimitBytes : Int,
  val timeLimit : Duration,
  code : String,
  filename : String)
  extends Test {

  def prepare(workingDir : Path) : Unit = {
    Files.write(workingDir.resolve(filename), code.getBytes)
  }

  val command = List("/usr/bin/scala", filename)

  val image = "cs220/scala"

}

