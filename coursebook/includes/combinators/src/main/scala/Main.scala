
class StringProc(val f: List[String] => Option[List[String]]) {

  def ++(other: StringProc): StringProc = new StringProc(input => {
    (f(input), other.f(input)) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(output1), Some(output2)) => Some(output1 ++ output2)
    }
  })

  def >>>(other: StringProc): StringProc = new StringProc(input => {
    f(input) match {
      case None => None
      case Some(output) => other.f(output)
    }
  })

  def ||(other: StringProc): StringProc = new StringProc(input => {
    f(input) match {
      case None => other.f(input)
      case Some(output) => Some(output)
    }
  })
 
}


object Main {

  def filter(pred: String => Boolean) = new StringProc(lines => Some(lines.filter(pred)))

  def cut(index: Int, sep: String) = new StringProc(lines => {
    val splitLines = lines.map(_.split(sep))
    if (splitLines.exists(line => line.length < index)) {
      None
    }
    Some(splitLines.map(cols => cols(index)))
  })

  def sort(index: Int, sep: String) = new StringProc(lines => {
    val splitLines = lines.map(_.split(sep))
    if (splitLines.exists(line => line.length < index)) {
      None
    }
    else {
      val sortedSplitLines = splitLines.sortBy(cols => cols(index))
      Some(sortedSplitLines.map(cols => cols.mkString(sep)))
    }
  })

  def head(n: Int) = new StringProc(lines => Some(lines.take(n)))

}
