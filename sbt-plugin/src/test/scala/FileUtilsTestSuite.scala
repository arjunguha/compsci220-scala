import cmpsci220.plugin._
import java.nio.file._

class FileUtilsTestSuite extends org.scalatest.FunSuite {

  test("find FileUtilsTestSuite.scala") {
    val r = FileUtils.findFiles("")(_.getFileName.toString == "FileUtilsTestSuite.scala")
    assert(r.length == 1)
  }

}