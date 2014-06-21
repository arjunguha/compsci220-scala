package cs220

import java.nio.file.{Paths, Files, Path}
import org.apache.commons.codec.digest.DigestUtils

package object submission {

  def isSubPath(base : Path, str: String) : Boolean = {
    val resolvedPath = base.resolve(Paths.get(str))
    resolvedPath.startsWith(base)
  }

  def assertAllSubPaths(base : Path, paths : Iterable[String]) : Unit = {
    paths.foreach { p =>
      if (!isSubPath(base, p)) {
       throw new IllegalArgumentException(s"$p must be a sub-path of $base")
      }
    }
  }

  def readAndHash(base : Path)(filename : String)
    : (String, (Array[Byte], String)) = {
    val path = base.resolve(Paths.get(filename))
    val data = Files.readAllBytes(path)
    val md5 = DigestUtils.md5Hex(data)
    (filename, (data, md5))
  }



}