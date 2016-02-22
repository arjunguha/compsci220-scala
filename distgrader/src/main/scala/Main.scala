package grading

import java.net.InetAddress

import grading.Messages.{Passed, ContainerExit}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  import scala.collection.JavaConversions._

  val localhost = InetAddress.getLocalHost

  import scala.concurrent._
  import scala.concurrent.duration._
  import akka.actor.{Props, ActorSystem}

  sealed trait Command
  case object Grade extends Command
  case object Extract extends Command
  case object Worker extends Command
  case object InitImage extends Command
  case object UploadWorker extends Command
  case object StartController extends Command
  case object StartWorker extends Command
  case object Summarize extends Command



  case class Config(command: Command, opts: Map[String, String], ints: Map[String, Int])

  val parser = new scopt.OptionParser[Config]("grader") {

    def key(name: String) = {
      opt[String](name).required
        .action((x, c)  => c.copy(opts = c.opts + (name -> x)))
    }

    def int(name: String) = {
      opt[Int](name).required
        .action((x, c)  => c.copy(ints = c.ints + (name -> x)))
    }


    cmd("grade")
      .action((_, cfg) => cfg.copy(command = Grade))
      .text("Grade an assignment")
      .children(key("name"))

    cmd("extract")
      .action((_, cfg) => cfg.copy(command = Extract))
     .children(key("src"), key("dst"))

    cmd("worker")
      .action((_, cfg) => cfg.copy(command = Worker))
      .text("Start a worker process on this machine (requires Docker locally)")

    cmd("start-worker")
      .action((_, cfg) => cfg.copy(command = Worker))
      .text("Start workers on Google Compute Engine")
      .children(key("prefix"), key("controller"), int("n"))

    cmd("summarize")
      .action((_, cfg) => cfg.copy(command = Summarize))
      .children(key("root"))

  }

  parser.parse(args, Config(Grade, Map(), Map())) match {
    case None => {
      println(parser.usage)
      System.exit(1)
    }
    case Some(config) =>{
      val opts = config.opts
      val ints = config.ints
      println(opts)
      config.command match {
        case Grade => {
          opts("name") match {
            case "hw1" => GradeHW1.main()
            case "hw2" => GradeHW2.main()
            case "hw3" => GradeHW3.main()
            case "discussion1" => GradeDiscussion1.main()
            case _ => {
              println("Unknown assignment")
              System.exit(1)
            }
          }
        }
        case Worker => {
          import akka.actor.{Props, ActorSystem}
          val ip = Await.result(InstanceMetadata.getPrivateIP(ExecutionContext.Implicits.global), 15.seconds)
          val config = AkkaInit.remotingConfig(ip, 5000)
          val system = ActorSystem("worker", config)
          val workerActor = system.actorOf(Props[WorkerActor], name=s"worker")
        }
        case InitImage => CreateImage.init()
        case UploadWorker => CreateImage.uploadWorker()
        case StartController => {
          val system = ActorSystem("controller", AkkaInit.remotingConfig(opts("ip"), 5000))
          val controllerActor = system.actorOf(Props[ControllerActor], name="controller")
        }
        case StartWorker => {
          import ExecutionContext.Implicits.global
          val name = opts("prefix")
          val controllerHost = opts("controller")
          val n = ints("n")
          val lst = 0.until(n).map(n => CreateImage.createWorker(s"$name-$n", controllerHost))
          Await.result(Future.sequence(lst), Duration.Inf)
        }
        case Extract => {
          val src = opts("src")
          val dst = opts("dst")
          Scripting.extract(src, dst)
        }
        case Summarize => {
          import Scripting._
          import Messages.{Passed, Failed, DidNotRun}
          val root = opts("root")
          val byTest = assignments(root).map(p => readRubric(p.resolve("grading.json")).tests.toList)
            .flatten.groupBy(_._1)

          for ((test, results) <- byTest) {
            val passed = results.filter(_._2.isInstanceOf[Passed]).size
            val failed = results.filter(_._2.isInstanceOf[Failed]).size
            val didNotRun = results.filter(_._2.isInstanceOf[DidNotRun]).size
            val rate = ((passed.toDouble / results.length.toDouble) * 100.0).toInt
            println(s"$test\n  $passed passed, $failed failed, $didNotRun did not run (success rate: $rate%)")
          }
        }

      }
    }
  }

}
