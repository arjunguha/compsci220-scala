
import java.nio.file.{Paths, Files, Path}
import org.scalatest.fixture.FunSuite
import org.scalatest.time.{Span, Seconds}
import org.scalatest.concurrent.{ScalaFutures, PatienceConfiguration}
import scala.concurrent._
import scala.concurrent.duration._
import cs220.submission._
import top._

class IntegrationSuite extends FunSuite with ScalaFutures {

  val confFile = "./src/test/files/Integration/settings.conf"

 // Controls timeout of whenReady and other features from ScalaFutures.
  implicit override val patienceConfig =
    PatienceConfig(Span(60, Seconds), Span(5, Seconds))

  // implicit val timeout = Timeout(15.seconds)

  type FixtureParam = Top

  override def withFixture(test: OneArgTest) = {
    // val map = test.configMap
    // val dockerUrl = map.getWithDefault("dockerUrl", "http://localhost:4243")

    // val settings = TopSettings(Paths.get(confFile))

    val theFixture = new Top(confFile)
    super.withFixture(test.toNoArgTest(theFixture))
  }

  test("getting an assignment that exists should succeed") { top =>
    top.getAssignment("asgn1", "step1")
  }

  test("getting a test suite that exists should succeed") { top =>
    assert(top.getTestSuite("asgn1", "step1").length == 2)
  }

}