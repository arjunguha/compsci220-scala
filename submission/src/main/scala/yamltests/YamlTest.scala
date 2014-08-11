package cs220.submission.yamltests

import java.nio.file.{Path, Files}
import cs220.submission.Test
import scala.concurrent.duration._

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


object YamlTest {


  import org.yaml.snakeyaml._
  import constructor.Constructor
  import scala.collection.JavaConversions._

  private val ctor = new Constructor(classOf[TestSuiteBean])
  private val desc = new TypeDescription(classOf[TestSuiteBean])
  desc.putListPropertyType("tests", classOf[TestBean])
  ctor.addTypeDescription(desc)
  private val yaml = new Yaml(ctor)

  def apply(string : String) : List[Test] = {
    yaml.load(string) match {
      case suite : TestSuiteBean => {
        val memoryLimitBytes = suite.getMemoryLimit
        val timeLimit = suite.getTimeLimit.seconds
        val filename = suite.getFilename
        suite.getTests.toList.map { test =>
          new YamlTest("no description", 0, memoryLimitBytes, timeLimit,
                       test.getTest, filename)
        }
      }
    }
  }

}