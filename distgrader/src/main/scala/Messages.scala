package grading

object Messages {

  case object WorkerReady
  case object AreYouReady
  case class Run(image: String, timeout: Int, workingDir: String, command: Seq[String], volumes: Map[String, Array[Byte]])

  case class ContainerExit(code: Int, stdout: String, stderr: String)

}
