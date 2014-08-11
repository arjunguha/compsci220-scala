import org.scalatest.fixture.FunSuite
import org.scalatest.concurrent.{ScalaFutures, PatienceConfiguration}
import org.scalatest.time.{Span, Seconds}
import scala.concurrent._
import scala.concurrent.duration._
import cs220.submission.sandbox.Sandbox
import java.nio.file.Paths

class TestSandbox(url : String) extends Sandbox(url) {

  def test(cmd : String*)
    (implicit ec : ExecutionContext) = {
    this(workingDir = Paths.get("./src/test/files/sandbox"),
         mountPoint = "/data",
         image = "ubuntu:14.04",
         command = cmd.toList,
         memoryBytes = 1024 * 1024 * 20,
         timeout = 3.second)
  }

}

trait SandboxFixture extends FunSuite with ScalaFutures {

  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  // Controls timeout of whenReady and other features from ScalaFutures.
  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(1, Seconds))

  type FixtureParam = TestSandbox

  override def withFixture(test: OneArgTest) = {
    val map = test.configMap
    val url = map.getWithDefault("url", "http://localhost:4243")
    val theFixture = new TestSandbox(url)
    super.withFixture(test.toNoArgTest(theFixture))
  }

}

