import scala.scalajs.js.JSApp
import org.scalajs.dom.document
import scalatags.JsDom.all._

object Example2 extends JSApp {
  def main(): Unit = {
    document.body.appendChild(p("Hello, 220!").render)
  }
}