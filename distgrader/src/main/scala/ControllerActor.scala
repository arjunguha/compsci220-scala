package grading

import scala.concurrent.Promise


class ControllerActor extends akka.actor.Actor with akka.actor.ActorLogging {

  import akka.actor.{ActorRef, Terminated}
  import java.nio.file.Paths
  import Messages._

  import this.context._

  val availableWorkers = scala.collection.mutable.Set[ActorRef]()
  val busyWorkers = scala.collection.mutable.Map[ActorRef, (Run, ActorRef)]()
  val pendingJobs = scala.collection.mutable.Queue[(Run, ActorRef)]()

  def startJobs(): Unit = {
    if (availableWorkers.isEmpty || pendingJobs.isEmpty) {
      ()
    }
    else {
      val worker = availableWorkers.head
      availableWorkers -= worker
      val (run, p) = pendingJobs.dequeue
      worker ! run
      busyWorkers += worker -> (run, p)
      startJobs()
    }
  }

  override def receive = {
    case akka.actor.Status.Failure(exn) => {
      busyWorkers.get(sender) match {
        case Some((run, p)) => {
          log.info(s"Failure from ${sender.path} $exn")
          p ! akka.actor.Status.Failure(exn)
          busyWorkers -= sender
          availableWorkers += sender
          startJobs()
        }
        case None => {
         log.warning(s"Stray failure from ${sender.path}")}
        }
    }
    case WorkerReady => {
      availableWorkers += sender
      context.watch(sender)
      log.info(s"New worker connected: ${sender.path}")
      startJobs()
    }
    case Terminated(actorRef) => {
      if (availableWorkers.contains(actorRef)) {
        availableWorkers -= actorRef
        log.info(s"Worker died: ${actorRef.path}")
      }
      else {
        busyWorkers.get(actorRef) match {
          case Some((run, p)) => {
           log.warning(s"Worker ${actorRef.path} died running job")
            pendingJobs.enqueue((run, p))
            startJobs()
          }
          case None => log.warning(s"Received stray Terminated(${actorRef.path})")
          }
      }
    }
    case exit : ContainerExit => {
      busyWorkers.get(sender) match {
        case Some((run, p)) => {

          p ! exit
          // p.success(exit)
          availableWorkers += sender
          busyWorkers -= sender
          startJobs()
        }
        case None => {
         log.warning(s"Received stray ContainerExit")
        }
      }
    }
    case run : Run => {
      val p = Promise[ContainerExit]()
      pendingJobs.enqueue(run -> sender)
      startJobs()
    }
  }


  def findExistingWorkers(): Unit = {
    import GCE.Implicits._
    import scala.collection.JavaConversions._

    val auth = SimpleAuth(Paths.get("cred.json"))

    implicit val compute = SimpleCompute("umass-cmpsci220", "us-east1-b", auth)

    val expectedSig = CreateImage.workerSignature()

    for (instance <- compute.compute.instances().list()) {
      val items = instance.getMetadata.getItems()
      (if (items != null) items.toList.find(_.getKey == "signature") else None) match {
        case Some(metadata) => {
          val sig = metadata.getValue()
          if (true) { // expectedSig == upickle.default.read[WorkerInstanceSignature](sig)) {
            val ip = instance.getNetworkInterfaces.get(0).getNetworkIP()
            log.info(s"Found candidate worker at $ip")
            context.actorSelection(s"akka.tcp://worker@$ip:5000/user/worker") ! AreYouReady

          }
        }
        case None => ()
      }
    }

  }

  findExistingWorkers()



}
