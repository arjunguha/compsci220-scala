package cmpsci220.graphics

import javafx.scene.{paint => p}

/** The type of colors
 *
 * There are a few predefined colors in the [[cmpsci220.graphics]] package.
 * You can also use the [[cmpsci220.graphics.rgb]] function to create your own
 * colors.
 *
 * @group Colors
 */
trait Color {

  private[graphics] def paint : p.Paint

}

private[graphics] class ColorImpl(val paint : p.Paint, text : String)
  extends Color {

  override def equals(obj : Any) : Boolean = obj match {
    case obj : ColorImpl => paint == obj.paint
    case _ => false
  }

  override def toString() : String = text

  override def hashCode() : Int = paint.hashCode

}
