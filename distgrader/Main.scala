package grading

import java.net.InetAddress

import grading.Messages.ContainerExit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  import scala.collection.JavaConversions._

  val localhost = InetAddress.getLocalHost

  import scala.concurrent._
  import scala.concurrent.duration._
  import akka.actor.{Props, ActorSystem}


  args match {
    case Array("extract", src, dst) => {
      val scripting = new grading.Scripting("10.8.0.6")
      import scripting._
      extract(src, dst)
      scripting.system.terminate()
    }

    case Array("hw1") => GradeHW1.main()
    case Array("hw2") => GradeHW2.main()
    case Array("worker") => {
      import akka.actor.{Props, ActorSystem}
      val ip = Await.result(InstanceMetadata.getPrivateIP(ExecutionContext.Implicits.global), 15.seconds)
      val config = AkkaInit.remotingConfig(ip, 5000)
      val system = ActorSystem("worker", config)
      val workerActor = system.actorOf(Props[WorkerActor], name=s"worker")
    }
    case Array("init-image") => CreateImage.init()
    // case Array("docker") => Grading.init()
    case Array("upload-worker") => CreateImage.uploadWorker()
    case Array("start-worker", name, controllerHost, number) => {
      import ExecutionContext.Implicits.global
      val lst = 0.until(number.toInt).map(n => CreateImage.createWorker(s"$name-$n", controllerHost))
      Await.result(Future.sequence(lst), Duration.Inf)


    }
    case Array("start-controller", ip) => {
      val system = ActorSystem("controller", AkkaInit.remotingConfig(ip, 5000))
      val controllerActor = system.actorOf(Props[ControllerActor], name="controller")
    }
  }
}
