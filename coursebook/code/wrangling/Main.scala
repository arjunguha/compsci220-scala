import edu.umass.cs.CSV
object Main extends App {
  val data = CSV.fromFile("congress-terms.csv").tail.filter(x => x(1) == "house")
  // Sizes
  println(data.groupBy(x => x(0)).mapValues(_.length))
  // Count # of republicans and democrats
  println(data.filter(x => x(11) == "2013-01-03")
      .groupBy(x => x(9))
      .mapValues(_.length))
}
