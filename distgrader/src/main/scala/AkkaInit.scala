package grading

object AkkaInit {

  import com.typesafe.config.{Config, ConfigFactory}
  import scala.collection.JavaConversions._

  def remotingConfig(hostname: String, port: Int): Config = {
    val conf = ConfigFactory.parseMap(Map[String, Object]("hostname" -> hostname, "port" -> new Integer(port)))
      .atKey("tcp").atKey("netty").atKey("remote").atKey("akka")
    ConfigFactory.defaultApplication.withFallback(conf)
  }

}
