package grading

import com.spotify.docker.client.messages.{HostConfig, ContainerConfig}


class WorkerActor extends akka.actor.Actor {

  import java.nio.file.{Files, Paths, Path}
  import context.dispatcher
  import scala.concurrent.Await
  import scala.concurrent.duration._
  import Messages._
  import com.spotify.docker.client.DefaultDockerClient

  val controllerHost = Await.result(InstanceMetadata.metadata("controller-host"), 10.seconds)

  val controller = context.actorSelection(s"akka.tcp://controller@$controllerHost:5000/user/controller")

  val docker = DefaultDockerClient.builder.uri("http://localhost:2376").build

  println(docker.version)
  controller ! WorkerReady

  override def receive = {
    case Run(image, timeout, volumes) => {
      val dir = Files.createTempDirectory(null)

      docker.pull(image)
      val r = docker.createContainer(ContainerConfig.builder.image(image).build)
      //docker.startContainer(r.id, HostConfig.builder.
    }
    case (n: Int) => {
      println(s"Received $n")
      sender ! s"Received $n"
    }
  }

}
