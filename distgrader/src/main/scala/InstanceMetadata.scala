package grading

object InstanceMetadata {

  import scala.concurrent.{Future, ExecutionContext}
  import upickle.default._
  import Implicits._

  private lazy val httpClient = new com.ning.http.client.AsyncHttpClient()

/*
 */

  case class Network(networkInterface: List[Iface])
  case class Iface(ip: String)

  def metadata(key: String)(implicit ec: ExecutionContext): Future[String] = {
    httpClient.prepareGet(s"http://metadata.google.internal/0.1/meta-data/attributes/$key").execute()
      .future.map(_.getResponseBody)
  }

  def getPrivateIP(implicit ec: ExecutionContext): Future[String] = {
    httpClient.prepareGet("http://metadata.google.internal/0.1/meta-data/network").execute().future.map { resp =>
      read[Network](resp.getResponseBody).networkInterface match {
        case List(Iface(ip)) => ip
        case _ => ???
      }
    }
  }

}
