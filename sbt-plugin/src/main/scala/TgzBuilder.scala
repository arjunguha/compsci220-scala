package cmpsci220.plugin

import java.nio.file._
import java.io._
import java.nio.file.StandardCopyOption._
import org.apache.commons.io.IOUtils
import org.apache.commons.compress.archivers.tar._
import org.apache.commons.compress.compressors.gzip._

class TgzBuilder(target: Path) {

  private var isClosed = false

  private val tmp = Files.createTempFile(Paths.get(""), null, ".tgz")
  private val fileOut = new FileOutputStream(tmp.toFile)
  private val bufOut = new BufferedOutputStream(fileOut)
  private val gzOut = new GzipCompressorOutputStream(bufOut)
  private val tarOut = new TarArchiveOutputStream(gzOut)

  def close(): Unit = {
    require(!isClosed, ".close already called")
    tarOut.finish()
    tarOut.close()
    gzOut.finish()
    gzOut.close()
    bufOut.close()
    fileOut.close()
    Files.move(tmp, target, REPLACE_EXISTING)
    isClosed = true
  }

  def add(src: Path): Unit = {
    require(!isClosed, ".close already called")
    val srcFile = src.toFile
    val entry = new TarArchiveEntry(srcFile)
    tarOut.putArchiveEntry(entry)
    if (srcFile.isFile) {
      IOUtils.copy(new FileInputStream(srcFile), tarOut)
    }
    tarOut.closeArchiveEntry()
  }

  def write(path: String, data: Array[Byte]): Unit = {
    require(!isClosed, ".close already called")
    val entry = new TarArchiveEntry(path)
    entry.setSize(data.length)
    tarOut.putArchiveEntry(entry)
    IOUtils.copy(new ByteArrayInputStream(data), tarOut)
    tarOut.closeArchiveEntry()
  }

}

object TgzBuilder {

  def apply(target: String) = new TgzBuilder(Paths.get(target))

}