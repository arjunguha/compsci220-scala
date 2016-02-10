package grading

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

  trait TestCase {

    def thenCompile(description: String, body: String, score: Int = 10): TestCase

    def thenRun(description: String,  body: String, score: Int = 10): TestCase
  }


  def testWithSbt(scripting: Scripting, dir: Path, prefix: String, report: Rubric)(body: TestCase => Unit): Future[Rubric] = {

    import scripting._
    import scripting.system.dispatcher

    class RootTestCase extends TestCase {
      def run(map: Map[String, Result]): Future[Map[String, Result]] = {
        Future.sequence(children.map(child => child.run(map))).map(lst => lst.foldLeft(map)(combineResults))
      }

      var children = List[TestCaseImpl]()

      def thenFoo(description: String, score: Int, body: String, verb: String): TestCaseImpl = {
        val test = new TestCaseImpl(description, score, {
          val zip = ZipBuilder()
            .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
            .add(s"object GradingMain extends App { $prefix $body }".getBytes, "src/main/scala/GradingMain.scala")
            .build()
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

    }


    class TestCaseImpl(key: String, score: Int, body: => Future[Result]) extends TestCase {

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

      var children = List[TestCaseImpl]()

      def thenFoo(description: String, score: Int, body: String, verb: String): TestCaseImpl = {
        val test = new TestCaseImpl(description, score, {
          val zip = ZipBuilder()
            .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
            .add(s"object GradingMain extends App { $prefix $body }".getBytes, "src/main/scala/GradingMain.scala")
            .build()
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

    }

    val root = new RootTestCase()
    body(root)
    root.run(report.tests).map(x => Rubric(x))
  }

}
