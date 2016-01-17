object Main extends App {
  import pdf._
  val base = "../website/_site"
  PDF.loan(PDF.open("student.pdf")) { pdf =>
    val pageRanges = pdf.toc
      .map({ bookmark => (bookmark.title, bookmark.pageNumber) })
      .sliding(2)
      .map({ case Stream((title, start), (_, nextStart)) => (title, start, nextStart - 1) })
      .filter({ case (title, _, _) => title.startsWith("file:") })
      .map({ case (title, start, stop) => (title.drop(5), start, stop) })

    for ((title, start, stop) <- pageRanges) {
      val section = PDF.create(pdf.pages.slice(start, stop))
      println(s"Saving $base/$title.pdf")
      section.save(s"$base/$title.pdf")
      section.close
    }
  }

}