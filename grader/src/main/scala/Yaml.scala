package grader

import org.yaml.snakeyaml._

object GradingYaml {

 import constructor.Constructor

  private val ctor = new Constructor(classOf[FeedbackBean])
  private val desc = new TypeDescription(classOf[FeedbackBean])
  desc.putListPropertyType("rubric", classOf[TestResultBean])
  ctor.addTypeDescription(desc)
  val yaml = new Yaml(ctor)


}
