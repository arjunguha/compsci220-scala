package grading

object Settings {
  import com.typesafe.config.ConfigFactory
  private val conf = ConfigFactory.load().getConfig("grading")
  val projectId = conf.getString("project-id")
}
