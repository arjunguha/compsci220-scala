package cs220.submission.grader

import cs220.submission.messages._
import akka.actor.{ActorSystem, ActorRef, Props}
import akka.util.Timeout
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import akka.testkit.{TestKit, TestActorRef}
import akka.pattern.ask

import org.scalatest.fixture.FunSuite
import org.scalatest.time.{Span, Seconds}
import org.scalatest.concurrent.{ScalaFutures, PatienceConfiguration}
import scala.concurrent._
import scala.concurrent.duration._

class GraderActorSuite extends FunSuite with ScalaFutures {

  val image = "ubuntu:14.04"

  val memLimit = 100L * 1024L * 1024L
  val timeLimit = 5.seconds

 // Controls timeout of whenReady and other features from ScalaFutures.
  implicit override val patienceConfig =
    PatienceConfig(Span(60, Seconds), Span(5, Seconds))

  implicit val timeout = Timeout(15.seconds)

  case class FixtureParam(testkit : TestKit, grader : ActorRef,
                          system : ActorSystem)

  override def withFixture(test: OneArgTest) = {
    val map = test.configMap
    val dockerUrl = map.getWithDefault("dockerUrl", "http://localhost:4243")

    val config = new GraderConfig(ConfigFactory.empty
      .withValue("temp-dir", ConfigValueFactory.fromAnyRef("."))
      .withValue("docker-url", ConfigValueFactory.fromAnyRef(dockerUrl))
      .withValue("hard-timeout", ConfigValueFactory.fromAnyRef("10s")))

    val testkit = new TestKit(ActorSystem())
    import testkit._

    // True concurrency needed  to test
    val grader = system.actorOf(Props(classOf[GraderActor], config))
    val theFixture = FixtureParam(testkit, grader, testkit.system)
    try {
      super.withFixture(test.toNoArgTest(theFixture))
    }
    finally {
      testkit.shutdown()
    }
  }

  test("creating the actor should succeed") { config =>
    // error won't be reported normally, I think ...
  }

  test("running a simple command should succeed") { config =>
    import config._

    val result = grader ? Submit(Map(), image, List("/bin/uname"),
                                 memLimit, timeLimit)
    whenReady(result.mapTo[Complete]) { r =>
      assert(r.code == 0)
      assert(r.stdout == "Linux\n")
      assert(r.stderr == "")
    }
  }

  test("timeout setting should work (5 seconds)") { config =>
    import config._
    val result = grader ? Submit(
      Map(),
      image,
      List("bash", "-c", "while true; do true; done"),
      memLimit,
      timeLimit)
    Await.result(result.mapTo[DidNotFinish], 8.seconds)
  }

  test("should be able to read from the data volume") { config =>
    import config._
    // Without the newline Docker's logs do not pick up the data!!
    val result = grader ? Submit(
      Map("test" -> "CS220 test data\n".getBytes),
      image,
      List("/bin/cat", "test"),
      memLimit,
      timeLimit)
    whenReady(result.mapTo[Complete]) { r =>
      assert(r.code == 0)
      assert(r.stdout == "CS220 test data\n")
      assert(r.stderr == "")
    }
  }

}