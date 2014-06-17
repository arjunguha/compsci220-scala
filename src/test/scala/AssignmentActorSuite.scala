package cs220.submission.assignments

import akka.actor.{ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import java.nio.file.Paths
import akka.testkit.{TestKit, TestActorRef}
import akka.pattern.ask

import org.scalatest.fixture.FunSuite
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent._
import scala.concurrent.duration._

class AssignmentActorSuite extends FunSuite with ScalaFutures {

  val conf = Paths.get("src/test/files/AssignmentActorSuite.conf").toFile()

  val config = new AssignmentActorConfig(ConfigFactory.parseFile(conf))

  implicit val timeout = Timeout(1000.millis)

  type FixtureParam = TestKit

  override def withFixture(test: OneArgTest) = {
    val theFixture = new TestKit(ActorSystem())
    try {
      super.withFixture(test.toNoArgTest(theFixture))
    }
    finally {
      theFixture.shutdown()
    }
  }

  test("query a non-existent assignment should fail") { testkit =>
    import testkit._
    val ref = TestActorRef(new AssignmentActor(config))
    whenReady(ref ? GetAssignmentMetadata("ghost", "blah")) { v =>
      assert(v == InvalidAssignment("ghost", "blah"))
    }
  }

  test("assignment metadata can be retreived") { testkit =>
    import testkit._
    val ref = TestActorRef(new AssignmentActor(config))
    val res = ref ? GetAssignmentMetadata("asgn1", "step1")
    whenReady(res.mapTo[AssignmentMetadata]) { v =>
      assert(v.submit == List("hello.scala"))
    }
  }

  test("assignment boilerplate hashes correctly") { testkit =>
    import testkit._
    val ref = TestActorRef(new AssignmentActor(config))
    val res = ref ? GetAssignmentMetadata("asgn1", "step2")
    whenReady(res.mapTo[AssignmentMetadata]) { v =>
      assert(v.boilerplate.length == 1)
      assert(v.boilerplate(0)._1 == "test-boilerplate.txt")
      assert(v.boilerplate(0)._2 == "a2693021c73339b3b2c3cf78cc91a34c")
    }
  }

  test("assignment boilerplate transfers correctly") { testkit =>
    import testkit._
    val ref = TestActorRef(new AssignmentActor(config))
    val res = ref ? GetAssignmentBoilerplate("asgn1", "step2")
    whenReady(res.mapTo[AssignmentBoilerplate]) { v =>
      assert(v.boilerplate.length == 1)
      assert(v.boilerplate(0)._1 == "test-boilerplate.txt")
      assert(new String(v.boilerplate(0)._2) ==
             "If this file is missing, the assignment cannot be created.")
    }
  }


}