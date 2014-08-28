import org.scalatest.FunSuite
import org.scalatest.concurrent.{ScalaFutures, PatienceConfiguration}
import org.scalatest.time.{Span, Seconds}
import scala.concurrent._
import scala.concurrent.duration._

trait RichObservableFixture extends FunSuite with ScalaFutures {

  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  // Controls timeout of whenReady and other features from ScalaFutures.
  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(1, Seconds))

}

