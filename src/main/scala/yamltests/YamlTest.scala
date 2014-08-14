package cs220.submission.yamltests

import java.nio.file.{Path, Files}
import cs220.submission.{Test, Assignment}
import scala.concurrent.duration._

private[yamltests] class YamlTest(
  val description : String,
  val points : Int,
  val memoryLimitBytes : Int,
  val timeLimit : Duration,
  val image : String,
  val command : List[String],
  code : String)
  extends Test {

  def prepare(workingDir : Path, asgn : Assignment) : Unit = {
    Files.write(workingDir.resolve("yamltest"), code.getBytes)
  }

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
        suite.getTests.toList.map { test =>
          new YamlTest(description = test.getDesc,
                       points = 0,
                       memoryLimitBytes = memoryLimitBytes,
                       timeLimit = timeLimit,
                       image = suite.getImage,
                       code = test.getTest,
                       command = suite.getCommand.toList)
        }
      }
    }
  }

}