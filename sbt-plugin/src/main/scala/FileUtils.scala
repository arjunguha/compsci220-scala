package cmpsci220.plugin

import java.io.IOException
import java.nio.file._
import attribute.BasicFileAttributes
import FileVisitResult.CONTINUE


object FileUtils {

  def fileNameHasSuffix(path: Path, suffix: String): Boolean = {
    path.getFileName.toString.endsWith(suffix)
  }

  def findFiles(root: String)(pred: Path => Boolean): Seq[Path] =
    findFiles(Paths.get(root))(pred)

  def findFiles(root: Path)(pred: Path => Boolean): Seq[Path] = {
    val r = collection.mutable.Buffer[Path]()
    Files.walkFileTree(root, new FileVisitor[Path] {

      def visitFile(file: Path, attrs: BasicFileAttributes) = {
        if (pred(file)) {
          r += file
        }
        CONTINUE
      }

      def postVisitDirectory(dir: Path, exc: IOException) = CONTINUE

      def preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = CONTINUE

      def visitFileFailed(file: Path, exc: IOException) = CONTINUE

    })
    r
  }

  def listDir(path: Path, glob: String): List[Path] = {
    import scala.collection.JavaConversions._
    val stream = Files.newDirectoryStream(path, glob)
    val lst = stream.toList
    stream.close
    lst
  }

  def resourceToByteArray(name: String): Array[Byte] = {
    import org.apache.commons.io.IOUtils
    val stream = this.getClass.getClassLoader().getResourceAsStream(name)
    val buf = IOUtils.toByteArray(stream)
    stream.close
    buf
  }



}