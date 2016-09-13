package grading

import java.io.FileInputStream
import java.nio.file.{Files, Paths, Path}

import com.google.api.client.http.InputStreamContent
import com.google.api.services.compute.model._
import org.apache.commons.codec.digest.DigestUtils

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Try, Success, Failure}

case class WorkerInstanceSignature(jar: String, init: String)


object CreateImage {


  import ExecutionContext.Implicits.global

  import scala.collection.JavaConversions._

  val auth = SimpleAuth(Paths.get("cred.json"))
  
  val projectId = Settings.projectId
  val zone = "us-east1-b"

  implicit val myCompute = SimpleCompute(projectId, "us-east1-b", auth)
  implicit val storage = SimpleStorage(projectId, auth)

  val compute = myCompute.compute

  import GCE.Implicits._

  val bucket = "umass-compsci220"

  def workerSignature(): WorkerInstanceSignature = {
    WorkerInstanceSignature(DigestUtils.md5Hex(Files.readAllBytes(Paths.get("target/distgrader.jar"))),
      DigestUtils.md5Hex(new String(Files.readAllBytes(Paths.get("scripts/init-worker.sh")))))
  }

  def createWorker(name: String, controllerHost: String): Future[String] = {
    val script = new String(Files.readAllBytes(Paths.get("scripts/init-worker.sh")))

    val sig = workerSignature()

    val inst = SimpleInstance(name = name,
      sourceImage = "global/images/worker-template",
      isPreemptible = true,
      metadata = Map("startup-script" -> script,
                     "controller-host" -> controllerHost,
                     "signature" -> upickle.default.write(sig)))
    compute.instances().insert(inst).map { op =>
      compute.instances().get(projectId, zone, name).execute.getNetworkInterfaces.head.getNetworkIP
    }
  }

  def uploadWorker(): Unit = {
    import com.google.api.services.storage.model.{Bucket, StorageObject}

    if (!storage.storage.buckets().list().exists(_.getName == bucket)) {
      storage.storage.buckets().insert(bucket, new Bucket().setName(bucket)).execute
    }

    val jarPath = Paths.get("target/distgrader.jar")
    val hash = DigestUtils.md5Hex(Files.readAllBytes(jarPath))

    Try(storage.storage.objects().get(bucket, "distgrader.jar").execute) match {
      case Failure(_) => ()
      case Success(obj) => {
        if (obj.getMetadata != null && obj.getMetadata.get("md5") == hash) {
          println(s"Already uploaded worker.jar")
          return
        }
      }
    }

    storage.storage.objects().insert(bucket,
      new StorageObject().setName("distgrader.jar").setContentDisposition("attachment").setMetadata(Map("md5" -> hash)),
      new InputStreamContent("application/octet-stream", new FileInputStream(jarPath.toFile))).execute

    println("Uploaded worker.jar")

  }

  def init(): Unit = {


    if (compute.instances().list().exists(_.getName == "docker-template")) {
      println("Removing instance ...")
      val oper = compute.instances().delete("docker-template")
      println(Await.result(oper, Duration.Inf))

    }

    if (compute.disks().list().exists(_.getName == "docker-template")) {
      println("Removing disk...")
      Await.result(compute.disks.delete(projectId, zone, "docker-template").future, Duration.Inf)
    }

    val startupScript = new String(Files.readAllBytes(Paths.get("scripts/init-container.sh")))
    val startupScriptModifiedTime = Files.getLastModifiedTime(Paths.get("scripts/init-container.sh")).toMillis

    val needsRefresh = compute.images().list().find(_.getName == "worker-template") match {
      case None => true
      case Some(image) => image.getDescription != startupScriptModifiedTime.toString
    }

    if (needsRefresh == false) {
      println("Image is up to date. Not refreshing")
      return
    }

    if (compute.images().list().exists(_.getName == "worker-template")) {
      println("Removing image...")
      Await.result(compute.images().delete("worker-template"), Duration.Inf)
    }

    val inst = SimpleInstance(name = "docker-template",
                              autoDeleteDisk = false,
                              tags = Seq(),
                              metadata = Map("startup-script" -> startupScript))
    val r = Await.result(compute.instances().insert(inst), Duration.Inf)
    println(s"Response is $r")
    while (compute.instances().getSerialPortOutput(projectId, zone, inst.name).execute()
             .getContents.contains("Finished running startup script /var/run/google.startup.script") == false) {
      Thread.sleep(10000)
    }

    println("Deleting instance...")
    Await.result(compute.instances().delete(projectId, zone, inst.name).future, Duration.Inf)

    println("Creating snapshot...")
    Await.result(compute.images()
      .insert(projectId, new Image().setName("worker-template").setSourceDisk(s"zones/$zone/disks/${inst.name}")
                                    .setDescription(startupScriptModifiedTime.toString))
      .globalFuture, Duration.Inf)


    println("Deleting disk...")
    Await.result(compute.disks().delete("docker-template"), Duration.Inf)

    println(r)

  }


}
