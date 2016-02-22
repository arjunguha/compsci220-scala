package grading

import scala.concurrent.Promise
import scala.util.{Success, Try}


class ControllerActor extends akka.actor.Actor with akka.actor.ActorLogging {

  import akka.actor.{ActorRef, Terminated}
  import java.nio.file.Paths
  import Messages._
  import scala.concurrent._
  import scala.concurrent.duration._

  import this.context._

  private object Tick

  private val availableWorkers = scala.collection.mutable.Set[ActorRef]()
  private val busyWorkers = scala.collection.mutable.Map[ActorRef, Job]()
  private val pendingJobs = scala.collection.mutable.Queue[Job]()

  private class Job(val label: String, val command: Run, val respondTo: ActorRef) {

    import java.time.Instant

    private val running = scala.collection.mutable.Map[ActorRef, Instant]()

    def start(worker: ActorRef)(implicit ec: ExecutionContext): Unit = {
        if (running.isEmpty == false) {
          log.info(s"Retrying $label")
        }
        assert(running.contains(worker) == false)
        val t = Instant.now()
        worker ! command
       running += (worker -> t)
    }

    def complete(result: Try[ContainerExit])(implicit ec: ExecutionContext): Unit = {
      result match {
        case scala.util.Failure(exn) => respondTo ! akka.actor.Status.Failure(exn)
        case Success(x) => respondTo ! x
      }
      for (worker <- running.keys) {
        busyWorkers -= worker
        availableWorkers += worker
      }
      running.clear()
    }

    def isLate(): Boolean = {
      val now = Instant.now()
      running.forall { case (actorRef, startTime) =>
        startTime.plusSeconds(command.timeout + 30).isBefore(now)
      }
    }

  }



  def startJobs(): Unit = {
    if (availableWorkers.isEmpty || pendingJobs.isEmpty) {
      log.info(s"${pendingJobs.length} jobs queued, ${busyWorkers.size} busy workers. ${availableWorkers.size} idle workers.")
    }
    else {
      val worker = availableWorkers.head
      availableWorkers -= worker
      val job = pendingJobs.dequeue
      job.start(worker)
      log.info(s"Sent a job to $worker (${job.label})")
      busyWorkers += worker ->job
      startJobs()
    }
  }

  akka.pattern.after(30.seconds, context.system.scheduler) {
    Future {
      self ! Tick
    }
  }

  override def receive = {
    case Tick => {
      for ((ref, job) <- busyWorkers) {
        if (job.isLate() && !pendingJobs.contains(job)) {
          println(s"${job.label} is taking a long time (on $ref)")
        }
        pendingJobs.enqueue(job)
      }
      startJobs()
      akka.pattern.after(30.seconds, context.system.scheduler) {
        Future {
          self ! Tick
        }
      }
    }
    case akka.actor.Status.Failure(exn) => {
      busyWorkers.get(sender) match {
        case Some(job) => {
          log.info(s"Failure from ${sender.path} $exn")
          job.complete(scala.util.Failure(exn))
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
          case Some(job) => {
           log.warning(s"Worker ${actorRef.path} died running job")
            pendingJobs.enqueue(job)
            startJobs()
          }
          case None => log.warning(s"Received stray Terminated(${actorRef.path})")
          }
      }
    }
    case exit : ContainerExit => {
      busyWorkers.get(sender) match {
        case Some(job) => {
          job.complete(Success(exit))
          log.info(s"Result from $sender (${job.label}")
          startJobs()
        }
        case None => {
         log.warning(s"Received stray ContainerExit")
        }
      }
    }
    case (label: String, run : Run) => {
      val p = Promise[ContainerExit]()
      pendingJobs.enqueue(new Job(label, run, sender))
      startJobs()
    }
  }

}
