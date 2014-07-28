package cs220.submission.yamltests.parser

import cs220.submission.yamltests.Test
import org.yaml.snakeyaml._
import constructor.Constructor


private[parser] object Parser {

  val ctor = new Constructor(classOf[TestSuiteBean])
  val desc = new TypeDescription(classOf[TestSuiteBean])
  desc.putListPropertyType("tests", classOf[TestBean])
  ctor.addTypeDescription(desc)
  val yaml = new Yaml(ctor)

  def load(string : String) : List[Test] = {
    import scala.collection.JavaConversions._
    yaml.load(string) match {
      case suite : TestSuiteBean =>
        suite.getTests.toList.map { test => Test(test.getTest) }
    }
  }

}