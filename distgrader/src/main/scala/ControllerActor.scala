package grading

import akka.actor.PoisonPill

import scala.concurrent.Promise

case class JobComplete(jobRef: akka.actor.ActorRef)
case class Start(worker: akka.actor.ActorRef)
case object EnqueueJob

private class Job(label: String,
                  command: Messages.Run,
                  respondTo: scala.concurrent.Promise[Messages.ContainerExit],
                  controller: akka.actor.ActorRef)
  extends akka.actor.Actor with akka.actor.ActorLogging {

  import akka.actor.{Terminated, ActorRef, Status}
  import Messages._
  import java.time.Instant
  import scala.concurrent.duration._
  import scala.concurrent._
  import context.dispatcher

  private object Tick

  private var tries = 0
  private var activeWorker: Option[ActorRef] = None

  def complete(): Unit = {
    controller ! JobComplete(self)
    activeWorker match {
      case Some(w) => controller ! WorkerReady(w)
      case None => ()
    }
    activeWorker = None
    log.info(s"Stopping job $label ($self)")
    self ! PoisonPill
  }

  def receive = {
    case Terminated => {
      log.info(s"$sender terminated while running $label")
      activeWorker = None
      controller ! EnqueueJob
    }
    case Start(worker) => {
      context.watch(worker)
      activeWorker match {
        case Some(_) => {
          log.error("Job already running")
          controller ! WorkerReady(worker)
        }
        case None => {
          tries = tries + 1
          activeWorker = Some(worker)
          worker ! command
          akka.pattern.after((command.timeout + 5).seconds, context.system.scheduler) { Future(self ! Tick) }
        }
      }
    }
    case Status.Failure(exn : SerializedException) => {
      log.error(s"Received serialized failure from $sender: ${exn.exn}")
      respondTo.failure(exn)
      complete()
    }
    case Status.Failure(exn) => {
      log.error(s"Received failure from $sender: $exn")
      respondTo.failure(exn)
      complete()
    }
    case result: ContainerExit => {
      log.info(s"Got ContainerExit from $sender to ($self)")
      respondTo.success(result)
      complete()
    }
    case Tick => {
      if (tries == 2) {
        respondTo.failure(throw new Exception("Test case failed"))
        complete()
      }
      else {
        log.warning(s"No response from client, retrying $tries")
        activeWorker = None
        controller ! EnqueueJob
      }
    }
  }

}

class ControllerActor extends akka.actor.Actor with akka.actor.ActorLogging {

  import akka.actor.{Props, ActorRef, Terminated}
  import Messages._

  private val availableWorkers = scala.collection.mutable.Set[ActorRef]()
  private val runningJobs = scala.collection.mutable.Set[ActorRef]()
  private val pendingJobs = scala.collection.mutable.Queue[ActorRef]()


  def startJobs(): Unit = {
    log.info(s"${pendingJobs.length} waiting / ${runningJobs.size} busy / ${availableWorkers.size} idle")
    if (!availableWorkers.isEmpty && !pendingJobs.isEmpty) {
      val worker = availableWorkers.head
      availableWorkers -= worker
      val job = pendingJobs.dequeue
      job ! Start(worker)
      runningJobs += job
      startJobs()
    }
  }

  override def receive = {
    case JobComplete(job) => {
      runningJobs -= job
    }
    case WorkerReady(worker) => {
      availableWorkers += worker
      context.watch(worker)
      log.info(s"New worker connected: $worker")
      startJobs()
    }
    case Terminated(actorRef) => {
      if (availableWorkers.contains(actorRef)) {
        availableWorkers -= actorRef
        log.info(s"Worker died: ${actorRef.path}")
      }
    }
    case EnqueueJob => {
      pendingJobs.enqueue(sender)
      startJobs()
    }
    case (label: String, p: Any, run : Run) => {
      val job = context.actorOf(Props(new Job(label, run, p.asInstanceOf[Promise[ContainerExit]], self)))
      pendingJobs.enqueue(job)
      startJobs()
    }
  }

}
