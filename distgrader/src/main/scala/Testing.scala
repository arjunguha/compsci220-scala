package grading

trait TestCase {

  def thenCompile(description: String, body: String, score: Int = 10): TestCase

  def thenRun(description: String,  body: String, score: Int = 10): TestCase
}

trait TestFramework {
  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, content: String): Unit
  def body(root: TestCase): Unit
  val assignmentRoot: String
  val selfIP: String
  val gradingJson = "grading.json"
}

object SBTTesting {

  import scala.concurrent._
  import scala.concurrent.duration._
  import Messages._
  import edu.umass.cs.zip._
  import java.nio.file.Path

  private def combineResults(m1: Map[String, Result], m2: Map[String, Result]) = {
    val keys = (m1.keySet ++ m2.keySet).toList
    keys.map(k => (m1.get(k), m2.get(k)) match {
      case (Some(Passed(score)), _) => k -> Passed(score)
      case (_, Some(Passed(score))) => k -> Passed(score)
      case (Some(failed@Failed(_, _, _, _)), _) => k -> failed
      case (_, Some(failed@Failed(_, _, _, _))) => k -> failed
      case (Some(v@DidNotRun(_, _)), _) => k -> v
      case (_, Some(v@DidNotRun(_, _))) => k -> v
      case (None, None) => throw new Exception("impossible")
    }).toMap
  }


  trait TestCaseLike extends TestCase {

    val scripting: Scripting
    val dir: Path
    val builder: (ZipBuilder, String) => Unit

    import scripting._
    import scripting.system.dispatcher

    var children = List[TestCaseImpl]()

    def thenFoo(description: String, score: Int, body: String, verb: String): TestCaseImpl = {
      val test = new TestCaseImpl(scripting, dir, builder, description, score, {
        val zipBuilder = ZipBuilder()
          .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
        builder(zipBuilder, body)
        val zip = zipBuilder.build()
        scripting.run(45, Seq("sbt", verb), zip, label = s"$description for $dir").map(_.toResultByCode(score))
      })
      children = test :: children
      test
    }


    def thenCompile(description: String, body: String, score: Int = 10): TestCaseImpl = {
      thenFoo(description, score, body, "compile")
    }

    def thenRun(description: String, body: String, score: Int = 10): TestCaseImpl = {
      thenFoo(description, score, body, "run")
    }

    def size(): Int = 1 + children.map(_.size()).sum
  }

  class TestCaseImpl(val scripting: Scripting, val dir: Path, val builder: (ZipBuilder, String) => Unit,
                     key: String, score: Int, body: => Future[Result])
    extends TestCaseLike {

    import scripting._
    import scripting.system.dispatcher

    def parentFailed(parent: String, map: Map[String, Result]): Map[String, Result] = {
      children.foldLeft(map + (key -> DidNotRun(score, parent))) {
        case (map, child) => {
          child.parentFailed(parent, map)
        }
      }
    }

    def run(map: Map[String, Result]): Future[Map[String, Result]] = {
      map.get(key) match {
        case Some(failed@Failed(_, _, _, _)) => {
          Future(children.foldLeft(map + (key -> failed)) {
            case (map, child) => {
              child.parentFailed(key, map)
            }
          })
        }
        case Some(passed@Passed(_)) => {
          val m = map + (key -> passed)
          Future.sequence(children.map(child => child.run(m)))
            .map(lst => lst.foldLeft(m)(combineResults))
        }
        case Some(didNotRun@DidNotRun(_, _)) => {
          println("ERROR SHOULD NOT HAVE HAPPENED")
          assert(false)
          null
        }
        case None => {
          body.flatMap(r => this.run(map + (key -> r)))
        }
      }
    }

  }

  class RootTestCase(val scripting: Scripting, val dir: Path,
                     val builder: (ZipBuilder, String) => Unit) extends TestCaseLike {
    import scripting.system.dispatcher

    def run(map: Map[String, Result]): Future[Map[String, Result]] = {
      Future.sequence(children.map(child => child.run(map))).map(lst => lst.foldLeft(map)(combineResults))
    }

  }

  def testWithSbt(scripting: Scripting, dir: Path, builder: (ZipBuilder, String) => Unit, report: Rubric)(body: TestCase => Unit): Future[Rubric] = {

    import scripting._
    import scripting.system.dispatcher



    val root = new RootTestCase(scripting, dir, builder)
    body(root)
    root.run(report.tests).map(x => Rubric(x))
  }


  def distributedTesting(framework: TestFramework): Unit = {
    import framework._
    val scripting = new grading.Scripting(selfIP)
    import scripting.system.dispatcher
    val lst = Scripting.assignments(assignmentRoot).map(dir => {
      Scripting.updateState(dir.resolve(gradingJson)) { case report =>
          val root = new RootTestCase(scripting, dir, zipBuilder)
          body(root)
          root.run(report.tests).map(results => Rubric(results))
      }
    })
    Await.result(Future.sequence(lst), Duration.Inf)
  }

}
