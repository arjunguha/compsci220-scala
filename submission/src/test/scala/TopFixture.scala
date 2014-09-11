import java.nio.file.{Paths, Files, Path}
import org.scalatest.fixture.FunSuite
import org.scalatest.time.{Span, Seconds}
import org.scalatest.concurrent.{ScalaFutures, PatienceConfiguration}
import scala.concurrent._
import scala.concurrent.duration._
import plasma.grader.top.Top

trait TopFixture extends FunSuite with ScalaFutures {

  private val confFile = "./submission/src/test/files/TopSuite/assignments/settings.conf"

  val submits = Paths.get("./submission/src/test/files/TopSuite/submissions")

  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

 // Controls timeout of whenReady and other features from ScalaFutures.
  implicit override val patienceConfig =
    PatienceConfig(Span(60, Seconds), Span(5, Seconds))

  type FixtureParam = Top

  override def withFixture(test: OneArgTest) = {
    val theFixture = new Top(confFile)
    super.withFixture(test.toNoArgTest(theFixture))
  }

}