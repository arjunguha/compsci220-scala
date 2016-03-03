package grading

import akka.actor.Status
import com.spotify.docker.client.messages.{HostConfig, ContainerConfig}

class ServerMonitorActor(serverUrl: String, worker: akka.actor.ActorRef)
  extends akka.actor.Actor with akka.actor.ActorLogging {

  import Messages._
  import akka.actor._
  import scala.concurrent.duration._
  import context.dispatcher

  def notifyServer(): Unit = {
    val f = context.actorSelection(serverUrl).resolveOne(30.seconds)
    f.onSuccess {
      case ref => {
        log.info(s"Found server $ref")
        ref ! WorkerReady(worker)
        context.watch(ref)
      }
    }
    f.onFailure { case _ => notifyServer() }
  }

  notifyServer()

  def receive = {
    case Terminated(actorRef) => {
      log.info("Server terminated")
      notifyServer()
    }
  }

}

class WorkerActor extends akka.actor.Actor with  akka.actor.ActorLogging {

  import akka.actor._
  import java.nio.file.{Files, Paths, Path}
  import scala.util.{Try, Success, Failure}
  import context.dispatcher
  import scala.concurrent.Await
  import scala.concurrent.duration._
  import Messages._
  import com.spotify.docker.client.DefaultDockerClient


  val docker = DefaultDockerClient.builder.uri("http://localhost:2376").build

  val controllerHost = Await.result(InstanceMetadata.metadata("controller-host"), 10.seconds)
  val controllerUrl = s"akka.tcp://controller@$controllerHost:5000/user/controller"

  context.actorOf(Props(new ServerMonitorActor(controllerUrl,self)))

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
          log.error(s"Docker job raised an exception $exn")
          sender ! Status.Failure(SerializedException(exn.toString))
        }
      }
    }
  }

}
