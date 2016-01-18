package grading

object Messages {

  case object WorkerReady
  case class Run(image: String, timeout: Int, volumes: Map[String, Array[Byte]])

  case class ContainerExit(code: Int, stdout: String, stderr: String)
}
