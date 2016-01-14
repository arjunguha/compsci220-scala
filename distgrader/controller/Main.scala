package grading

import java.net.InetAddress

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  import scala.collection.JavaConversions._

  val localhost = InetAddress.getLocalHost

  import scala.concurrent._
  import scala.concurrent.duration._
  import akka.actor.{Props, ActorSystem}


  args match {
    case Array("init-image") => CreateImage.init()
    case Array("docker") => Grading.init()
    case Array("upload-worker") => CreateImage.uploadWorker()
    case Array("start-worker", ip) => Await.result(CreateImage.createWorker("my-worker", ip), Duration.Inf)
    case Array("start-controller", ip) => {
      val system = ActorSystem("controller", AkkaInit.remotingConfig(ip, 5000))
      val controllerActor = system.actorOf(Props[ControllerActor], name="controller")
    }
  }
}
