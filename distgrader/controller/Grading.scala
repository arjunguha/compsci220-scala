package grading

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.core.DockerClientBuilder

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise, Future, ExecutionContext}

object Grading {
  import ExecutionContext.Implicits.global

  import scala.collection.JavaConversions._


  def asFuture[A, B](handler : ResultCallback[A] => B)(implicit ec: ExecutionContext): Future[List[A]] = {
    var result = List[A]()
    val promise = Promise[List[A]]()
    handler(new ResultCallback[A] {

      def onStart(closeable: java.io.Closeable): Unit = {}

      def onNext(r: A): Unit = {
        result = r :: result
      }

      def onError(throwable: Throwable) = promise.failure(throwable)

      def onComplete() = promise.success(result)

      def close() = {
        if (promise.isCompleted == false) {
          promise.failure(new RuntimeException("closed early"))
        }
      }
    })
    promise.future
  }


  def init(): Unit = {
    val docker = DockerClientBuilder.getInstance("http://10.240.0.3:2376").build()

    //docker.buildImageCmd().withDockerfile(
    println(docker.versionCmd().exec())
    val r = asFuture(docker.pullImageCmd("gcr.io/umass-cmpsci220/student").exec)
    println(Await.result(r, Duration.Inf))

    //val container = docker.createContainerCmd("gcr.io/umass-cmpsci220/student").withV
    //container

  }

}
