package grading

import java.io.ByteArrayOutputStream
import java.nio.file.{Files, Path}
import java.util.zip.{ZipEntry, ZipOutputStream}

class ZipBuilder() {

  val bytes = new ByteArrayOutputStream()
  val zip = new ZipOutputStream(bytes)
  val entries = scala.collection.mutable.Set[String]()
  var zipBytes: Array[Byte] = null


  def filterAdd(src: Path, dst: String, includeFile: Path => Boolean): ZipBuilder = {
    if (Files.isDirectory(src)) {
      import scala.collection.JavaConversions._
      val stream = Files.newDirectoryStream(src)
      for (p <- stream) {
        filterAdd(p, s"$dst/${p.getFileName.toString}", includeFile)
      }
      stream.close()
      this
    }
    else {
      if (includeFile(src)) {
        add(Files.readAllBytes(src), dst)
      }
      else {
        this
      }
    }
  }

  def add(src: Path, dst: String): ZipBuilder = filterAdd(src, dst, _ => true)

  def add(srcBytes: Array[Byte], dst: String): ZipBuilder = {
    assert(zipBytes == null, "cannot add more files after .build is invoked")
    assert(entries.contains(dst) == false)
    val e = new ZipEntry(dst.toString)
    zip.putNextEntry(e)
    zip.write(srcBytes, 0, srcBytes.length)
    zip.closeEntry()
    entries += dst
    this
  }

  def build(): Array[Byte] = {
    if (zipBytes == null) {
      zip.close()
      bytes.close()
      zipBytes = bytes.toByteArray
    }
    zipBytes
  }

}

object ZipBuilder {


  def apply(): ZipBuilder = new ZipBuilder()

}
