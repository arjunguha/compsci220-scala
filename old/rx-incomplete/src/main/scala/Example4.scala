import scala.scalajs.js.JSApp
import org.scalajs.dom._
import scalatags.JsDom.all._

object Example4 extends JSApp {

  var time = 0

  val currentTime = div().render

  def updateTime(): Unit = {
    time = time + 1
    currentTime.textContent = s"$time seconds since page loaded."
  }

  def resetTime(): Unit = {
    time = 0
  }

  def main(): Unit = {

    window.setInterval(updateTime _, 1000)
    val resetButton = input(`type` := "Button", `value` := "Reset", onclick := resetTime _).render

    document.body.appendChild(currentTime)
    document.body.appendChild(resetButton)
  }
}