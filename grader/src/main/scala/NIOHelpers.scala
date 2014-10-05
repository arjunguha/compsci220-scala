import java.nio.file._

object NIOHelpers {

  def dirFilter(p: Path => Boolean) = new DirectoryStream.Filter[Path] {
    def accept(entry: Path) = p(entry)
  }


}