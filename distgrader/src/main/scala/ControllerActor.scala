package grading

import scala.concurrent.Promise


class ControllerActor extends akka.actor.Actor with akka.actor.ActorLogging {

  import akka.actor.{ActorRef, Terminated}
  import java.nio.file.Paths
  import Messages._
  import scala.concurrent._
  import scala.concurrent.duration._

  import this.context._

  val availableWorkers = scala.collection.mutable.Set[ActorRef]()
  val busyWorkers = scala.collection.mutable.Map[ActorRef, (String, Run, ActorRef)]()
  val pendingJobs = scala.collection.mutable.Queue[(String, Run, ActorRef)]()

  def startJobs(): Unit = {
    if (availableWorkers.isEmpty || pendingJobs.isEmpty) {
      log.info(s"${pendingJobs.length} jobs queued, ${busyWorkers.size} busy workers. ${availableWorkers.size} idle workers.")
    }
    else {
      val worker = availableWorkers.head
      availableWorkers -= worker
      val (label, run, p) = pendingJobs.dequeue
      worker ! run
      log.info(s"Sent a job to $worker ($label)")
      busyWorkers += worker -> (label, run, p)
      startJobs()
    }
  }

  akka.pattern.after(30.seconds, context.system.scheduler) {
    Future {
      self ! "timer"
    }
  }

  var previouslyActive = List[String]()

  override def receive = {
    case "timer" => {
      for ((ref, (lbl, _, _)) <- busyWorkers) {
        if (previouslyActive.contains(lbl)) {
          println(s"$lbl is taking a long time (on $ref)")
        }
      }
      previouslyActive = busyWorkers.values.map(_._1).toList
      akka.pattern.after(30.seconds, context.system.scheduler) {
        Future {
          self ! "timer"
        }
      }
    }
    case akka.actor.Status.Failure(exn) => {
      busyWorkers.get(sender) match {
        case Some((label, run, p)) => {
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
    case WorkerReady(workerRef) => {
      availableWorkers += workerRef
      context.watch(workerRef)
      log.info(s"New worker connected: ${workerRef.path}")
      startJobs()
    }
    case Terminated(actorRef) => {
      if (availableWorkers.contains(actorRef)) {
        availableWorkers -= actorRef
        log.info(s"Worker died: ${actorRef.path}")
      }
      else {
        busyWorkers.get(actorRef) match {
          case Some((label, run, p)) => {
           log.warning(s"Worker ${actorRef.path} died running job")
            pendingJobs.enqueue((label, run, p))
            startJobs()
          }
          case None => log.warning(s"Received stray Terminated(${actorRef.path})")
          }
      }
    }
    case exit : ContainerExit => {
      busyWorkers.get(sender) match {
        case Some((label, run, p)) => {

          p ! exit
          // p.success(exit)
          availableWorkers += sender
          busyWorkers -= sender
          log.info(s"Result from $sender ($label)")
          startJobs()
        }
        case None => {
         log.warning(s"Received stray ContainerExit")
        }
      }
    }
    case (label: String, run : Run) => {
      val p = Promise[ContainerExit]()
      pendingJobs.enqueue((label, run, sender))
      startJobs()
    }
  }

}
