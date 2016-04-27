package grading


object Attendance {

  import edu.umass.cs.extras.Implicits._
  import edu.umass.cs.CSV
  import java.nio.file.{Paths, Files, Path}
  import java.time.temporal.ChronoUnit

  // Maps each student to the number of days late
  def processFile(filename: Path): Unit = {
    val header :: data = CSV.fromFile(filename)
    val newData = data
      .map(row => if (row(3).contains("No submission"))
                    row.updated(4, "0").updated(9, "Not submitted -- no credit")
                  else
                    row.updated(4, "100"))
    CSV.save(filename, header :: newData)
  }
  def fromDirectory(dir: String): Unit = {
    import scala.collection.JavaConversions._
    val stream = Files.newDirectoryStream(Paths.get(dir))
    val csvs = stream.filter(p => p.getFileName.toString.endsWith(".csv")).toList
    stream.close()
    for (file <- csvs) {
      processFile(file)
    }
  }

}
