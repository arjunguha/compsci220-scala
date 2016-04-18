import scala.scalajs.js
import org.scalajs.dom

import scalatags.JsDom
import scalatags.JsDom.all._
import rx._

object Lib {

  def currentTime(): Long = (new js.Date()).getTime.toLong

  def interval(milliseconds: Int): Rx[Long] = {
    val x = Var(currentTime())
    dom.window.setInterval(() => {
      x() = currentTime()
      }, milliseconds)
    x
  }

  implicit class rxIntFrag(rxValue: Rx[Int]) extends scalatags.jsdom.Frag {
    def render: dom.Text = {
      val node = dom.document.createTextNode(rxValue().toString)
      Obs(rxValue, skipInitial = true) {
        node.replaceData(0, node.length, rxValue().toString)
      }
      node
    }
  }

  implicit class rxLongFrag(rxValue: Rx[Long]) extends scalatags.jsdom.Frag {
    def render: dom.Text = {
      val node = dom.document.createTextNode(rxValue().toString)
      Obs(rxValue, skipInitial = true) {
        node.replaceData(0, node.length, rxValue().toString)
      }
      node
    }
  }

  def clicks(node: dom.Node): Var[Unit] = {
    val x = Var[Unit](())
    def callback(evt: dom.Event): Unit = {
      x() = ()
    }
    node.addEventListener("click", callback _)
    x
  }

  //implicit def rxAsJsFrag(rxValue: Rx[Int]) = new rxIntFrag(rxValue)

}
