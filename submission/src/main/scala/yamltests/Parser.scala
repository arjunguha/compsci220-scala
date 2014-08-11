package cs220.submission.yamltests

import cs220.submission.Test
import org.yaml.snakeyaml._
import scala.concurrent.duration._
import constructor.Constructor

private[yamltests] object Parser {

  val ctor = new Constructor(classOf[TestSuiteBean])
  val desc = new TypeDescription(classOf[TestSuiteBean])
  desc.putListPropertyType("tests", classOf[TestBean])
  ctor.addTypeDescription(desc)
  val yaml = new Yaml(ctor)

  def load(string : String) : List[Test] = {
    import scala.collection.JavaConversions._
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