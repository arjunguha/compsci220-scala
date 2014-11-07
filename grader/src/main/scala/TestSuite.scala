package grader

import java.nio.file.{Files, Paths, Path}
import scala.concurrent._
import scala.concurrent.duration._
import org.yaml.snakeyaml._
import scala.util.{Success, Failure}
import ExecutionContext.Implicits.global

object TestSuite {

  import constructor.Constructor

  private val ctor = new Constructor(classOf[FeedbackBean])
  private val desc = new TypeDescription(classOf[FeedbackBean])
  desc.putListPropertyType("rubric", classOf[TestResultBean])
  ctor.addTypeDescription(desc)
  private[grader] val yaml = new Yaml(ctor)


  def apply(filename: String)(genTests: Builder => Unit): Unit = {


    val path = Paths.get(filename)
    Files.deleteIfExists(path)

    val builder = new Builder()
    genTests(builder)

    val feedback = new FeedbackBean
    feedback.setTime(new java.util.Date().toString)
    import scala.collection.JavaConversions._
    feedback.setRubric(builder.testCases.map(_.run))
    feedback.getCumulative.setScore(feedback.getRubric.map(_.getScore).sum)
    feedback.getCumulative.setMaxScore(feedback.getRubric.map(_.getMaxScore).sum)

    System.err.println("Completed.")
    val dump = yaml.dumpAs(feedback, nodes.Tag.MAP, DumperOptions.FlowStyle.BLOCK)
    Files.write(path, dump.getBytes)

    sys.exit(0)
  }

}

private case class TestCase(description: String, points: Int, body: () => Boolean) {

  def run(): TestResultBean = {
    val bean = new TestResultBean
    bean.setCriterion(description)
    bean.setMaxScore(points)

    CancellableFuture.withTimeout(body, 10.seconds) match {
      case Success(true) => bean.setScore(points)
      case Success(false) => bean.setScore(0)
      case Failure(exn: ExecutionException) => {
        System.err.println(s"Timeout testing $description")
        bean.setScore(0)
      }
      case Failure(exn) => {
        System.err.println(s"Unexpected exception during testing $exn")
        bean.setScore(0)
      }
    }
    bean
  }
}

class Builder private[grader] () {

  private var maxPoints = 0
  private val tests = collection.mutable.Buffer[TestCase]()

  def test(description : String, points: Int = 10)(body : => Unit) : Unit = {
    maxPoints = maxPoints + points
    def wrappedBody(): Boolean = {
      try {
        body
        true
      }
      catch {
        case (exn : Throwable) => {
          println(s"Exception during grading $description")
          println(exn)
          false
        }
      }
    }
    tests += new TestCase(description, points, wrappedBody)
  }

  def fails(description : String, points: Int = 10)(body : => Unit) : Unit = {
    maxPoints = maxPoints + points
    def wrappedBody(): Boolean = {
      try {
        body
        false
      }
      catch {
        case (exn : Throwable) => true
      }
    }
    tests += new TestCase(description, points, wrappedBody)
  }

  private[grader] def testCases(): Seq[TestCase] = tests.toSeq
}

class TestSuite(filename: String) {

  import TestSuite._


  private val path = Paths.get(filename)
  if (Files.deleteIfExists(path)) {
    println(s"Deleted $filename before re-grading")
  }

  private def dump(feedback: FeedbackBean): Unit = {
    import scala.collection.JavaConversions._

    feedback.getCumulative.setScore(feedback.getRubric.map(_.getScore).sum)
    feedback.getCumulative.setMaxScore(feedback.getRubric.map(_.getMaxScore).sum)
    Files.write(path, yaml.dumpAs(feedback, nodes.Tag.MAP, DumperOptions.FlowStyle.BLOCK).getBytes)
  }


  // Blank file
  dump(new FeedbackBean())

  private def report(description: String, points: Int, maxPoints: Int): Unit = {


    val result = new TestResultBean()
    result.setCriterion(description)
    result.setScore(points)
    result.setMaxScore(maxPoints)

    val feedback = yaml.load(new String(Files.readAllBytes(path))) match {
      case feedback: FeedbackBean => feedback
    }

    feedback.setTime(new java.util.Date().toString)
    feedback.getRubric.add(result)
    dump(feedback)
  }

  def test(description : String, points: Int = 10)(body : => Unit) : Unit = {
    try {
      body
      report(description, points, points)
    }
    catch {
      case (exn : Throwable) => {
        println(s"Exception during grading $description")
        println(exn)
        report(description, 0, points)
      }
    }
  }

  def fails(description : String, points: Int = 10)(body : => Unit) : Unit = {
    try {
      body
      report(description, 0, points)
    }
    catch {
      case (exn : Throwable) => {
        report(description, points, points)
      }
    }
  }
}
