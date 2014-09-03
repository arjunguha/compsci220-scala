package cmpsci220

import javafx.scene.{paint => p}
import javafx.animation._
import javafx.event.{ActionEvent, EventHandler}
import javafx.util.Duration
import javafx.scene.paint.Color
import javafx.scene.input.KeyEvent
import javafx.scene._
import javafx.scene.canvas._
import javafx.stage.{Stage, WindowEvent}
import javafx.application.{Application, Platform}
import javafx.embed.swing.SwingFXUtils
import javax.imageio.ImageIO
import java.io.File
import scala.concurrent._
import scala.concurrent.duration.{Duration => ScalaDuration}
import ExecutionContext.Implicits.global

/** An API for drawing 2D images and animations.
 *
 */
package object graphics {

  import cmpsci220.graphics.Image.SimpleImage
  import main.Main.{preStart, setupCanvas}

  /** Ignores pressed keys
   *
   * @group Miscellaneous
   */
  def ignoreKey[T](key : String, state : T) : T = state

  /** Starts the program
   *
   * Starts the program in the {@code init} state and draws the state using
   * the {@code draw} argument. There are many optional arguments that you can
   * provide to customize the behavior of the program. For example, you can use
   * the {@code tick} argument to update the state to build an animation.
   *
   * @param init the initial state of the program
   * @param draw a function from states to [[Image]]s, which is called on
   *        each tick
   * @param width <i>(optional)</i> the width of the window; 400 by default
   * @param height <i>(optional)</i> the height of the window; 400 by default
   * @param tick <i>(optional)</i> a function to update the state; the default
   *             value is the identity function
   * @param refreshRate <i>(optional)</i> the number of times per second that
   *                    {@code draw} and {@code tick} will be applied; the
   *                    default value is 35 tickes per second
   * @return the last value of the state before the window is closed
   *
   * @group Starting the program
   */
  def animate[T](init : T,
                 draw : T => Image,
                 width : Int = 400,
                 height : Int = 400,
                 tick : T => T = identity[T] _,
                 keyPressed : (String, T) => T = ignoreKey[T] _,
                 keyReleased : (String, T) => T = ignoreKey[T] _,
                 refreshRate : Double = 35) : T = {
    val exit = Promise[Unit]()

    var state = init

    def start(stage : Stage) {

      val (gc, canvas) = setupCanvas(stage, "Animation", width, height)

      canvas.setOnKeyPressed(new EventHandler[KeyEvent] {
        override def handle(evt : KeyEvent) : Unit = {
          state = keyPressed(evt.getText(), state)
        }
      })

      canvas.setOnKeyReleased(new EventHandler[KeyEvent] {
        override def handle(evt : KeyEvent) : Unit = {
          state = keyReleased(evt.getText(), state)
        }
      })

      def onFinished = new EventHandler[ActionEvent] {
        override def handle(evt : ActionEvent) {
          gc.setFill(p.Color.WHITE)
          gc.fillRect(0, 0, width, height)
          draw(state).draw(gc)
          state = tick(state)
        }
      }

      val frame = new KeyFrame(Duration.seconds(1 / refreshRate), onFinished)
      val timeline = new Timeline(frame)
      timeline.setCycleCount(Animation.INDEFINITE)
      timeline.play()
      stage.showAndWait()
      timeline.stop()
      state
    }

    preStart(start, exit)
    Await.result(exit.future, ScalaDuration.Inf)
    state
  }

  /** Shows an image
   *
   * Shows an image in a window.
   *
   * @param image the image to draw
   * @param width <i>(optional)</i> the width of the window; 400 by default
   * @param height <i>(optional)</i> the height of the window; 400 by default
   *
   * @group Starting the program
   */
  def show(image : Image, width : Int = 400, height : Int = 400) : Unit = {
    val exit = Promise[Unit]()

    def start(stage : Stage) {
      val (gc, canvas) = setupCanvas(stage, "Image", width, height)
      image.draw(gc)
      stage.showAndWait()
    }
    preStart(start, exit)
    Await.result(exit.future, ScalaDuration.Inf)
  }

  /** Save an image to a file
   *
   * @group Miscellaneous
   */
  def saveImage(fileName : String, image : Image, width : Int = 400,
           height : Int = 400) : Unit = {
    def start(stage : Stage) {
      val (gc, canvas) = setupCanvas(stage, "Image", width, height)
      image.draw(gc)
      ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null),
                    "png",
                    new File(fileName))
      stage.hide()
    }
    preStart(start, Promise[Unit]())
  }

  /** Draws a line
   *
   * Draws a line from {@code (0,0)} to {@code (x,y)}.
   *
   * <b>Examples:</b>
   *
   * {@code line(100, 0, red)} draws a horizonal, red line.
   *
   * {@code line(0, 100, blue, 10)} draws a thick, vertical, blue line.
   *
   * @param x the x-coordinate of the end-point
   * @param y the y-coordinate of the end-point
   * @param color <i>(optional)</i> the color of the line; black by default
   * @param width <i>(optional)</i> the width of the line; 1 by default
   *
   * @group Images
   */
  def line(x : Double, y : Double,
           color : Color = black,
           width : Double = 1) : Image =
    new SimpleImage(s"line($x, $y, $color, $width)", gc => {
       gc.setStroke(color.paint)
       gc.setLineWidth(width)
       gc.strokeLine(0, 0, x, y)
    })

  /** A blank image
   *
   * This image does not appear. But, it is often useful as a base-case when
   * writing image-building functions.
   *
   * @group Images
   */
  val blank : Image = new SimpleImage("blank", gc => ())

  /** Draws a rectangle
   *
   * Draws a rectangle with width {@code width} and height {@code height}.
   *
   * <b>Examples:</b>
   *
   * {@code rect(100, 30, red)} draws a short, red rectangle
   * {@code rect(100, 100, blue)} draws a blue square
   *
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param color <i>(optional)</i> the color of the line; black by default
   *
   * @group Images
   */
  def rect(width : Double,
           height : Double,
           color : Color = black) : Image =
    new SimpleImage(s"rect($width, $height, $color)", gc => {
      gc.setStroke(color.paint)
      gc.strokeRect(0, 0, width, height)
    })

  /** Draws a filled rectangle
   *
   * Draws a filled rectangle with width {@code width} and height {@code height}.
   *
   * <b>Examples:</b>
   *
   * {@code fillRect(100, 30, red)} draws a short, red rectangle
   * {@code fillRect(100, 100, blue)} draws a blue square
   *
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param color <i>(optional)</i> the color of the line; black by default
   *
   * @group Images
   */
  def fillRect(width : Double,
               height : Double,
               color : Color = black) : Image =
    new SimpleImage(s"fillRect($width, $height, $color)", gc => {
      gc.setFill(color.paint)
      gc.fillRect(0, 0, width, height)
    })

  /** Draws a solid oval
   *
   * Draws an oval with width {@code width} and height {@code height}.
   *
   * <b>Examples:</b>
   *
   * {@code solidOval(10, 100, blue)} draws a tall and thin blue oval
   * {@code solidOval(100, 100, red)} draws a red circle with diameter 100
   *
   * @param width the width of the oval
   * @param height the height of the oval
   * @param color <i>(optional)</i> the color; black by default
   *
   * @group Images
   */
  def solidOval(width : Double,
                height : Double,
                color : Color = black) : Image =
    new SimpleImage(s"solidOval($width, $height, $color)", gc => {
      gc.setFill(color.paint)
      gc.fillOval(0, 0, width, height)
    })

  /** Draws an oval
   *
   * Draws an oval with width {@code width} and height {@code height}.
   *
   * <b>Examples:</b>
   *
   * {@code oval(10, 100, blue)} draws a tall and thin blue oval
   * {@code oval(100, 100, red)} draws a red circle with diameter 100
   *
   * @param weight the width of the oval
   * @param height the height of the oval
   * @param color <i>(optional)</i> the color of the oval; black by default
   *
   * @group Images
   */
  def oval(width : Double,
           height : Double,
            color : Color = black) : Image =
    new SimpleImage(s"oval($width, $height, $color)", gc => {
      gc.setStroke(color.paint)
      gc.strokeOval(0, 0, width, height)
    })

  private val radiansPerDegree = 2 * math.Pi / 360

  /** Draws a line at an angle.
   *
   * Draws a line from {@code (length,length)} of length {@code length}. The
   * angle between the x-axis and the line is given by {@code degrees}.
   *
   * <b>Examples</b>
   *
   * {@code angle(0, 10)} draws a horizontal line from (10, 10) to (20, 10)
   * {@code angle(90, 10)} draws a vertical line from (10, 10) to (10, 20)
   *
   * @param degrees the angle of the line, specified in degrees
   * @param length the length of the line
   * @param color <i>(optional)</i> the color; black by default
   * @param width <i>(optional)</i> the width; 1 by default
   *
   * @group Images
   */
  def angle(degrees : Double,
            length : Double,
            color : Color = black,
            width : Double = 1) : Image =
    new SimpleImage(s"angle($degrees, $length, $color, $width)", gc => {
      val radians = radiansPerDegree * degrees
      gc.setStroke(color.paint)
      gc.setLineWidth(width)
      gc.strokeLine(length, length, length + length * math.cos(radians),
                    length + length * math.sin(radians))

    })

  /** Overlays one image on top of another.
   *
   * Draws the {@code top} image on top of {@code bot}.
   *
   * <b>Examples:</b>
   *
   * {@code overlay(rect(10, 10, blue), rect(20, 20, red))} draws
   * a blue square inside a red square.
   *
   * @param top the image to draw on top
   * @param bot the image to draw on the bottom
   *
   * @group Images
   */
  def overlay(top : Image, bot : Image) : Image =
    new SimpleImage(s"overlay($top, $bot)", gc => {
      bot.draw(gc)
      top.draw(gc)
    })

  /** Moves an image
   *
   * Moves an image {@code x} pixels horizontally and {@code y} pixels
   * vertically.
   *
   * <b>Examples:</b>
   *
   * {@code overlay(move(rect(10, 10, blue), 5, 5), rect(20, 20, red))} draws
   * a blue square <i>centered</i> inside a red square.
   *
   * @param image the image to draw
   * @param x the x offset of the image
   * @param y the y offset of the image
   *
   * @group Images
   */
  def move(image : Image, x : Double, y : Double) : Image =
    new SimpleImage(s"move($image, $x, $y)", gc => {
      gc.translate(x, y)
      image.draw(gc)
    })

  /** A color defined by its red, green, and blue values
   *
   * @param red the amount of red, in the range {@code 0.0-1.0}
   * @param blue the amount of blue, in the range {@code 0.0-1.0}
   * @param green the amount of green, in the range {@code 0.0-1.0}
   *
   * @group Colors
   */
  def rgb(red : Double, green : Double, blue : Double) : Color =
    new ColorImpl(new p.Color(red, green, blue, 1.0 ),
                  s"rgb($red, $green, $blue)")

  /** The color black
   *
   * @group Colors
   */
  def black : Color = new ColorImpl(p.Color.BLACK, "black")

  /** The color red
   *
   * @group Colors
   */
  def red : Color = new ColorImpl(p.Color.RED, "red")

  /** The color blue
   *
   * @group Colors
   */
  def blue : Color = new ColorImpl(p.Color.BLUE, "blue")

  /** The color green
   *
   * @group Colors
   */
  def green : Color = new ColorImpl(p.Color.GREEN, "green")

}
