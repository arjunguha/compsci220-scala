package grading

import com.github.dockerjava.api.model.Volume

object WorkerMain extends App {

  import scala.concurrent._
  import scala.concurrent.duration._
  import akka.actor.{Props, ActorSystem}

  val ip = Await.result(InstanceMetadata.getPrivateIP(ExecutionContext.Implicits.global), 15.seconds)


  val config = AkkaInit.remotingConfig(ip, 5000)

  val system = ActorSystem("worker", config)



  val workerActor = system.actorOf(Props[WorkerActor], name=s"worker-$ip")
}
