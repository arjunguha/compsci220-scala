package cmpsci220

import graphics.main.Main
import javafx.scene.media.{MediaPlayer, Media}
import java.nio.file.{Paths, Files}
import scala.concurrent._

/** An API for playing sounds.
 *
 */
package object sounds {

  def play(filename : String) : Unit = {
    val path = Paths.get(filename)
    assert(Files.isRegularFile(path), s"$filename is not a file")
    assert(Files.isReadable(path), s"$filename cannot be opened")

    Main.preStart(stage => {
      val player = new MediaPlayer(new Media(path.toUri().toString()))
      player.play()
    },
    Promise[Unit]())
  }

}