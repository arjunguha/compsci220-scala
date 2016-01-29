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
    case Array("extract-hw1") => {
      val scripting = new grading.Scripting("10.8.0.6")
      import scripting._
      extract("hw1.zip", "hw1")
      scripting.system.terminate()
    }

    case Array("hw1") => {
      val scripting = new grading.Scripting("10.8.0.6")
      import java.nio.file._
      import scripting._

      import scripting.system.dispatcher

      val lst = assignments("hw1").map(dir => {
        val zip = ZipBuilder()
         .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
         .build()
        run(120, Seq("sbt", "compile"), zip)
          .map({
            case msg@ContainerExit(0, stdout, stderr) => {
              Files.write(dir.resolve("compile-ok.txt"), stdout.getBytes)
              Some(msg)
            }
            case msg@ContainerExit(n, stdout, stderr) => {
              Files.write(dir.resolve("compile-error-stdout.txt"), stdout.getBytes)
              Files.write(dir.resolve("compile-error-stderr.txt"), stderr.getBytes)
              println(s"Cannot compile $dir (exit $n)")
              Some(msg)
            }
          })
          .recover({
            case exn: Throwable => {
              println(s"Exception compiling $dir $exn")
              None
            }
          })
      })
      Await.result(Future.sequence(lst), Duration.Inf)
      //val r = run("gcr.io/umass-cmpsci220/student", 30, Seq("/bin/ls", "/"))
      //println(Await.result(r, Duration.Inf))
      scripting.system.terminate()
    }
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
