import scala.scalajs.js._
import org.scalajs.dom._
import scalatags.JsDom.all._
import rx._
import scalatags.rx.all._
import Lib._

object Example6 extends JSApp {
  def main(): Unit = {
    val t = interval(1000)
    val startTime = t.now
    document.body.appendChild(div(Rx { (t() - startTime) / 1000 },
                                  " seconds since page load").render)
  }
}