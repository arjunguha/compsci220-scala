package grading

import akka.actor.Terminated


class ControllerActor extends akka.actor.Actor with akka.actor.ActorLogging {

  import akka.actor.ActorRef
  import Messages._

  val workers = scala.collection.mutable.Set[ActorRef]()

  println(self.path)
  override def receive = {
    case WorkerReady => {
      workers += sender
      context.watch(sender)
      log.info(s"New worker connected: ${sender.path}")
    }
    case Terminated(actorRef) => {
      workers -= actorRef
      log.info(s"Worker died: ${actorRef.path}")
    }

    case (str: String) => println(s"Received $str")
  }

}
