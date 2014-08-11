package cs220.submission.student

import cs220.submission._
import messages._
import com.typesafe.config.ConfigFactory
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.{Paths, Files, Path}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import java.io.File
import scala.async.Async.{async, await}
import akka.pattern.{pipe, ask}
import org.apache.commons.codec.digest.DigestUtils

object Main extends App {

  val confFile = System.getenv("SUBMISSION_CONF")

  if (confFile == null) {
    println("Configuration error: SUBMISSION_CONF environment variable missing.")
    System.exit(1)
  }

  val settings = StudentSettings(Paths.get(confFile))

  implicit val system = ActorSystem("student")
  implicit val log = system.log
  import system.dispatcher

  implicit val timeout = Timeout(120.seconds)

  lazy val graderActor =
    system.actorOf(Props(classOf[grader.GraderActor], settings.grader))

  lazy val asgns =
    system.actorOf(Props(classOf[assignments.AssignmentActor], settings.assignments))

  lazy val studentActor =
    system.actorOf(Props(classOf[StudentActor], graderActor, asgns))

  try {
    args match {
      case Array("submit", asgn, step, dir) => {
        println(Await.result(studentActor ? SubmitLocal(asgn, step, dir),
                             Duration.Inf))
      }
      case Array("submit", asgn, step) => {
        println(Await.result(studentActor ? SubmitLocal(asgn, step, "."),
                             Duration.Inf))
      }
    }
  }
  finally {
    system.shutdown
    System.exit(0)
  }


}