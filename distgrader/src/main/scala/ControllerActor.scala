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

  private val running = scala.collection.mutable.Map[ActorRef, Instant]()

  def retick(): Unit = {
    akka.pattern.after(30.seconds, context.system.scheduler) {
      Future {
        self ! Tick
      }
    }
  }

  def complete(): Unit = {
    controller ! JobComplete(self)
    for (w <- running.keys) {
      controller ! WorkerReady(w)
    }
    running.clear()
    log.info(s"Stopping job $label ($self)")
    self ! PoisonPill
  }

  def receive = {
    case Terminated => {
      log.info(s"$sender terminated while running $label")
      running -= sender
      if (running.isEmpty) {
        controller ! EnqueueJob
      }
    }
    case Start(worker) => {
      context.watch(worker)
      log.info(s"${running.size} workers working on $self")
      if (running.isEmpty) {
        retick()
      }
      assert(running.contains(worker) == false)
      val t = Instant.now()
      worker ! command
      running += (worker -> t)
    }
    case Status.Failure(exn : SerializedException) => {
      log.error(s"Receivedfserializedailure from $sender: ${exn.exn}")
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
      val now = Instant.now()
      val allLate = running.forall {
        case (actorRef, startTime) => {
          val isLate = startTime.plusSeconds(command.timeout + 30).isBefore(now)
          if (isLate) {
            log.info(s"$label started on $actorRef at $startTime")
          }
          isLate
        }
      }

      if (allLate && running.size < 3) {
        controller ! EnqueueJob
      }
      retick()
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
