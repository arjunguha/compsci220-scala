import scala.scalajs.js.JSApp
import org.scalajs.dom._
import scalatags.JsDom.all._
import rx._
import scalatags.rx.all._
import Lib._

object Example5 extends JSApp {
  def main(): Unit = {
    val t = interval(1000)
    document.body.appendChild(div(t, " milliseconds since 1 Jan 1970").render)
  }
}