package grading

import akka.actor.Status
import com.spotify.docker.client.messages.{HostConfig, ContainerConfig}


class WorkerActor extends akka.actor.Actor with  akka.actor.ActorLogging {

  import java.nio.file.{Files, Paths, Path}
  import scala.util.{Try, Success, Failure}
  import context.dispatcher
  import scala.concurrent.Await
  import scala.concurrent.duration._
  import Messages._
  import com.spotify.docker.client.DefaultDockerClient


  val docker = DefaultDockerClient.builder.uri("http://localhost:2376").build

  val controllerHost = Await.result(InstanceMetadata.metadata("controller-host"), 10.seconds)

  context.actorSelection(s"akka.tcp://controller@$controllerHost:5000/user/controller") ! WorkerReady

  override def receive = {
    case AreYouReady => sender ! WorkerReady
    case Run(image, timeout, workingDir, command, volumes) => {
      log.info("Starting Docker job")
      Try(Worker.run(docker, image, timeout, workingDir, command, volumes)) match {
        case Success(result) => {
          log.info(s"Docker job aborted normally (exit code ${result.code}")
          sender ! result
        }
        case Failure(exn) => {
          log.error(s"Docker job raised an exception\n$exn")
          sender ! Status.Failure(exn)
        }
      }
    }
  }

}
