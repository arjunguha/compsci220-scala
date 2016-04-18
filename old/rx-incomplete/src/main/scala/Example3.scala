import scala.scalajs.js.JSApp
import org.scalajs.dom._
import scalatags.JsDom.all._

object Example3 extends JSApp {

  def main(): Unit = {
    var time = 0

    val currentTime = div().render

    window.setInterval(() => {
      time = time + 1
      currentTime.textContent = s"$time seconds since page loaded."
    }, 1000)

    document.body.appendChild(currentTime)
  }
}