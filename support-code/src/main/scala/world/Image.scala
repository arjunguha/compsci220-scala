package cmpsci220.graphics

import javafx.scene._
import javafx.scene.canvas._
import javafx.scene.paint._
import javafx.scene.shape._
import javafx.stage._

/** The type of images
 *
 * There are several functions in the [[cmpsci220.graphics]] package to create
 * simple shapes, such as [[cmpsci220.graphics.rect]], [[cmpsci220.graphics.oval]],
 * etc. You can make more complex images by manipulating and composing
 * simpler shapes. E.g., see the functions [[cmpsci220.graphics.move]] and
 * [[cmpsci220.graphics.overlay]].
 *
 * @group Images
 */
trait Image {

  private[graphics] def draw(gc : GraphicsContext)

}

// Why are these classes inside an object? Scaladoc displays all top-level
// classes, even if they are marked private. However, private objects are not
// documented.
private[graphics] object Image  {

  class SimpleImage (text : String, f : GraphicsContext => Unit) extends Image {

    def draw(gc : GraphicsContext) : Unit = {
      gc.save()
      f(gc)
      gc.restore()
    }

    override def toString() : String = text

  }

  case class Overlay (val top : Image, bot : Image,
                      x : Double, y : Double) extends Image {

    // Using SimpleImage would involve an unnecessary save and restore. This is
    // premature optimization.
    def draw(gc : GraphicsContext) : Unit = {
      gc.save()
      gc.translate(x, y)
      bot.draw(gc)
      gc.restore()
      top.draw(gc)
    }

    override def toString() = s"overlay($top, $bot, $x, $y)"

  }
}