package grading

object Settings {
  import com.typesafe.config.ConfigFactory
  private val conf = ConfigFactory.load().getConfig("grader")
  val projectId = conf.getString("project-id")

  val controllerIP = conf.getString("controller")
}
