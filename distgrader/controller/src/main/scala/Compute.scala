package grading

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import java.nio.file.{Files, Paths, Path}

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.compute.model.Metadata.Items
import com.google.api.services.compute.{ComputeScopes, Compute}

import com.google.api.services.compute.model._
import java.io.ByteArrayInputStream

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.collection.JavaConversions._
import scala.util.{Try, Success, Failure}

case class SimpleAuth(credentialsFile: Path) {
  val transport = GoogleNetHttpTransport.newTrustedTransport()
  val credential = GoogleCredential.fromStream(new ByteArrayInputStream(Files.readAllBytes(credentialsFile)))
    .createScoped(List(ComputeScopes.COMPUTE, ComputeScopes.DEVSTORAGE_FULL_CONTROL))
  val jsonFactory = JacksonFactory.getDefaultInstance()
}

case class SimpleCompute(project: String, zone: String, auth: SimpleAuth) {
  import auth._
  val compute = new Compute.Builder(transport, jsonFactory, credential).setApplicationName("myapp").build()
}

case class SimpleStorage(project: String, auth: SimpleAuth) {
  import com.google.api.services.storage.Storage
  import auth._
  val storage = new Storage.Builder(transport, jsonFactory, credential).setApplicationName("myapp").build()
}

case class SimpleInstance(
  name: String,
  tags: Seq[String] = Seq(),
  metadata: Map[String, String] = Map(),
  autoDeleteDisk: Boolean = true,
  sourceImage: String = "https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1404-trusty-v20151113")

object Implicits {

  import com.google.api.client.googleapis.services.AbstractGoogleClientRequest
  import com.google.api.services.storage.Storage

  private def nullListToSeq[T](lst: java.util.List[T]): Seq[T] = {
    if (lst == null) {
      Seq()
    }
    else {
      lst.toSeq
    }
  }

  implicit class RichBuckets(buckets: Storage#Buckets) {

    def list()(implicit ec: ExecutionContext, storage: SimpleStorage) = {
      nullListToSeq(buckets.list(storage.project).execute().getItems)
    }

  }

  implicit class RichObjects(objects: Storage#Objects) {

//    def exists(bucket: String, objectName: String)(implicit ec: ExecutionContext, storage: SimpleStorage) = {
//      Try(objects.get(bucket, objectName).execute()) match {
//        case Failure(_) => false
//        case Success(_) => true
//      }
//    }

  }

  implicit class RichOperation(req: AbstractGoogleClientRequest[Operation]) {

    def future(implicit ec: ExecutionContext, compute: SimpleCompute): Future[Operation] = {
      var op = req.execute()
      Future[Operation] {
        while (op.getStatus() != "DONE") {
          println(op.getStatus)
          Thread.sleep(1000)

          Try(compute.compute.zoneOperations().get(compute.project, compute.zone, op.getName).execute()) match {
            case Failure(_) => println(s"no status available :$op")
            case Success(op_) => op = op_
          }
        }
        println("Done.")
        op
      }
    }

    def globalFuture(implicit ec: ExecutionContext, compute: SimpleCompute): Future[Operation] = {
      var op = req.execute()
      Future[Operation] {
        while (op.getStatus() != "DONE") {
          println(op.getStatus)
          Thread.sleep(1000)

          Try(compute.compute.globalOperations().get(compute.project, op.getName).execute()) match {
            case Failure(_) => println(s"no status available :$op")
            case Success(op_) => op = op_
          }
        }
        println("Done.")
        op
      }
    }

  }

  implicit class RichInstances(instances: Compute#Instances) {

    def delete(name: String)(implicit ec: ExecutionContext, compute: SimpleCompute): Future[Operation] = {
      instances.delete(compute.project, compute.zone, name).future
    }

    def list()(implicit ec: ExecutionContext, compute: SimpleCompute): Seq[Instance] = {
      nullListToSeq(instances.list(compute.project, compute.zone).execute().getItems)
    }

    def insert(inst: SimpleInstance)(implicit ec: ExecutionContext, compute: SimpleCompute): Future[Operation] = {
      val intf = new NetworkInterface()
        .setNetwork(s"https://www.googleapis.com/compute/v1/projects/${compute.project}/global/networks/default")
        .setAccessConfigs(List(new AccessConfig().setType("ONE_TO_ONE_NAT").setName("External NAT")))

      val disk = new AttachedDisk()
        .setBoot(true)
        .setAutoDelete(inst.autoDeleteDisk)
        .setType("PERSISTENT")
        .setInitializeParams(new AttachedDiskInitializeParams()
          .setDiskType(s"https://www.googleapis.com/compute/v1/projects/${compute.project}/zones/${compute.zone}/diskTypes/pd-standard")
          .setSourceImage(inst.sourceImage))


      val metadata = inst.metadata.toSeq.map { case (key, value) => new Metadata.Items().setKey(key).setValue(value) }

      val instance = new Instance().setName(inst.name)
        .setMachineType(s"https://www.googleapis.com/compute/v1/projects/${compute.project}/zones/${compute.zone}/machineTypes/n1-standard-1")
        .setDisks(List(disk))
        .setNetworkInterfaces(List(intf))
        .setMetadata(new Metadata().setItems(metadata))
        .setTags(new Tags().setItems(inst.tags))
        .setServiceAccounts(List(new ServiceAccount().setEmail("default").setScopes(List(ComputeScopes.DEVSTORAGE_READ_ONLY))))

      instances.insert(compute.project, compute.zone, instance).future
    }

  }

  implicit class RichDisks(disks: Compute#Disks) {

    def delete(name: String)(implicit ec: ExecutionContext, compute: SimpleCompute): Future[Operation] = {
      disks.delete(compute.project, compute.zone, name).future
    }

    def list()(implicit ec: ExecutionContext, compute: SimpleCompute): Seq[Disk] = {
      nullListToSeq(disks.list(compute.project, compute.zone).execute().getItems)
    }

  }

  implicit class RichImages(images: Compute#Images) {

    def delete(name: String)(implicit ec: ExecutionContext, compute: SimpleCompute): Future[Operation] = {
      images.delete(compute.project, name).globalFuture
    }

    def list()(implicit ec: ExecutionContext, compute: SimpleCompute): Seq[Image] = {
      nullListToSeq(images.list(compute.project).execute().getItems)
    }

  }

}
