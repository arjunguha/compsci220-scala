package cmpsci220.graphics.main

import cmpsci220.graphics.Image
import javafx.animation._
import javafx.event.{ActionEvent, EventHandler}
import javafx.util.Duration
import javafx.scene.paint.Color
import javafx.scene.input.KeyEvent
import javafx.scene._
import javafx.scene.canvas._
import javafx.stage.{Stage, WindowEvent}
import javafx.application.{Application, Platform}
import scala.concurrent._
import scala.concurrent.duration.{Duration => ScalaDuration}
import ExecutionContext.Implicits.global

private[cmpsci220] object Main {

  private lazy val javafxEnabled = System.getenv("DISABLE_JAVAFX") match {
    case "TRUE" => false
    case "FALSE" => true
    case null => true
    case str =>
      sys.error(s"unexpected value for the DISABLE_JAVAFX envvar $str")
  }

  // Stops JavaFX from shutting down when all windows are closed. You cannot
  // restart JavaFX.
  Platform.setImplicitExit(false)

  private var realStart : Stage => Unit = null
  private var exit : Promise[Unit] = null
  private var launched = false

  // Why a nested class? Scaladoc documents all top-level classes, regardless
  // of visibility. However, private objects are not displayed.
  private class MyApplication extends Application {

    def start(mainStage : Stage) {
      val exit = Main.exit
      val realStart = Main.realStart
      Main.exit = null // free reference
      Main.realStart = null // free reference
      val stage = new Stage()
      setupExitHandler(stage, exit)
      realStart(stage)
    }
  }

  def preStart(start : Stage => Unit, exit : Promise[Unit]) : Unit = {
    if (!javafxEnabled) {
      exit.success(())
      return
    }

    if (!launched) {
      launched = true
      Main.realStart = start
      Main.exit = exit

      // The thread in which we call Application.launch becomes the
      // distinguished JavaFX thread. We create a new thread for JavaFX to avoid
      // conflicts with the main thread of a console application.
      val appThread = new Thread(new Runnable {
        def run() : Unit = {
          Application.launch(classOf[MyApplication])
        }
      }, "appThread")
      appThread.setDaemon(true)
      appThread.start()
    }
    else {
      Platform.runLater(new Runnable {
        def run() : Unit = {
          val stage = new Stage()
          setupExitHandler(stage, exit)
          start(stage)
        }
      })
    }
  }

  private def setupExitHandler(stage : Stage, exit : Promise[Unit]) : Unit = {
    def onCloseRequest = new EventHandler[WindowEvent] {
      override def handle(evt : WindowEvent) {
        exit.success(())
      }
    }
    stage.setOnCloseRequest(onCloseRequest)
  }

  def setupCanvas(stage : Stage, title : String, width : Int,
                  height : Int) : (GraphicsContext, Canvas) = {
    stage.setTitle(title)
    val root = new Group()
    stage.setScene(new Scene(root))
    val canvas = new Canvas(width, height)
    root.getChildren().add(canvas)
    val gc = canvas.getGraphicsContext2D()

    // Origin at the bottom left corner
    gc.translate(0, height)
    gc.scale(1, -1)
    gc.save()
    gc -> canvas
  }

}

