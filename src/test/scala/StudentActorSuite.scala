import cs220.submission._
import grader.GraderActor
import assignments.AssignmentActor
import messages.SubmitLocal
import student.{StudentActor, StudentSettings}
import java.nio.file.{Paths, Files, Path}

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

class StudentActorSuite extends FunSuite with ScalaFutures {

  val confFile = "./src/test/files/StudentActorSuite/settings.conf"

 // Controls timeout of whenReady and other features from ScalaFutures.
  implicit override val patienceConfig =
    PatienceConfig(Span(60, Seconds), Span(5, Seconds))

  implicit val timeout = Timeout(15.seconds)

  case class FixtureParam(testkit : TestKit, student : ActorRef,
                          system : ActorSystem)

  override def withFixture(test: OneArgTest) = {
    val map = test.configMap
    val dockerUrl = map.getWithDefault("dockerUrl", "http://localhost:4243")

    val settings = StudentSettings(Paths.get(confFile))

    val testkit = new TestKit(ActorSystem())
    import testkit._

    // True concurrency needed  to test
    val grader = system.actorOf(Props(classOf[GraderActor], settings.grader))
    val assignments = system.actorOf(Props(classOf[AssignmentActor], settings.assignments))
    val student = system.actorOf(Props(classOf[StudentActor], grader, assignments))
    val theFixture = FixtureParam(testkit, student, testkit.system)
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
}