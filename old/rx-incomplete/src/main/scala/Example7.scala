import scala.scalajs.js._
import org.scalajs.dom._
import scalatags.JsDom.all._
import rx._
import scalatags.rx.all._
import Lib._

object Example7 extends JSApp {

  def main(): Unit = {
    val t = interval(1000)
    val startTime = t.now

    val resetButton = input(`type` := "Button", `value` := "Reset").render

    val resetClicks: Rx[Unit] = clicks(resetButton)
    val resetTime = Rx {
      resetClicks()
      currentTime()
    }

    document.body.appendChild(div(Rx { (t() - resetTime()) / 1000 },
                                  " seconds since last reset.").render)
    document.body.appendChild(resetButton.render)

  }
}