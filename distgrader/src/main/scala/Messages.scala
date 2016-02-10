package grading

import java.text.DecimalFormat

object Messages {

  case class WorkerReady(actor: akka.actor.ActorRef)
  case object AreYouReady
  case class Run(image: String, timeout: Int, workingDir: String, command: Seq[String], volumes: Map[String, Array[Byte]])

  case class ContainerExit(code: Int, stdout: String, stderr: String) {

    def toResultByCode(maxScore: Int) = {
      if (code == 0) {
        Passed(maxScore)
      }
      else {
        Failed(maxScore, stdout.replaceAll("\u001B\\[[;\\d]*m", ""), stderr.replaceAll("\u001B\\[[;\\d]*m", ""), code)
      }
    }

  }

  case class Rubric(tests: Map[String, Result]) {

    def -(key: String): Rubric = Rubric(tests - key)

    override def toString(): String = {
      val  points = tests.values.map({
        case Passed(score) => score
        case _ => 0
      }).sum
      val pointsLost = tests.values.map({
        case failed: Failed => failed.maxScore
        case didNotRun: DidNotRun => didNotRun.maxScore
        case _ => 0
      }).sum

      val totalPoints = points + pointsLost

      val df = new DecimalFormat("#")
      val percent = (points.toDouble / totalPoints.toDouble) * 100.0
      val percentStr = df.format(percent)
      val summary = s"Percentage: $percentStr%\nPoints: $points / $totalPoints\n\nDetails:\n\n"
      val detail = tests.toSeq.map({ case (k, v) => {
        s"$k $v"
      } }).mkString("\n")

      summary + detail
    }

  }

  sealed trait Result
  case class Passed(score: Int) extends Result {

    override def toString(): String = {
      s"passed ($score/$score points)"
    }

  }

  case class Failed(maxScore: Int,
                    stdout: String,
                    stderr: String,
                    exitCode: Int) extends Result {

    override def toString(): String = {
      val fmtStdout = stdout.split("\n").map(str => "    " + str).mkString("\n")
      val fmtStderr = stderr.split("\n").map(str => "    " + str).mkString("\n")
      s"failed (0/$maxScore points)\nStandard output:$fmtStdout\n\nStandard error:\n\n$fmtStderr\n\nExit code: $exitCode\n"
    }


  }
  case class DidNotRun(maxScore: Int, dependsOn: String) extends Result {

    override def toString(): String = {
      s"did not run, because it depends on '${dependsOn}' (score $maxScore)"
    }

  }

  object MyJsonProtocol extends spray.json.DefaultJsonProtocol {
    import spray.json._

    implicit val PassedFormat = jsonFormat1(Passed)
    implicit val failedFormat = jsonFormat4(Failed)
    implicit val didNotRunFormat = jsonFormat2(DidNotRun)

   implicit object Result extends RootJsonFormat[Result] {
      def write(result: Result) = result match {
        case passed: Passed => JsObject("status" -> JsString("passed"), "detail" -> passed.toJson)
        case failed: Failed => JsObject("status" -> JsString("failed"), "detail" -> failed.toJson)
        case didNotRun: DidNotRun => JsObject("status" -> JsString("did not run"), "detail" -> didNotRun.toJson)
      }
      def read(value: JsValue) = value.asJsObject.getFields("status", "detail") match {
        case Seq(JsString("passed"), detail) => detail.convertTo[Passed]
        case Seq(JsString("failed"), detail) => detail.convertTo[Failed]
        case Seq(JsString("did not run"), detail) => detail.convertTo[DidNotRun]
        case _ => deserializationError("Color expected")
      }
    }

    implicit val rubricFormat = jsonFormat1(Rubric)

  }

}
