object GradingMain extends App {
  import org.scalatest.Args
  import org.scalatest.events.Event
  val suite = new GradingTests()
  val tests = suite.testNames.toArray.sortWith(_ < _)

  def eventHandler(event: Event): Unit = {
  }

  args.toList match {
    case List("num-tests") => println(tests.length)
    case List("run-test", nStr) => {
      val n = nStr.toInt
      val isOk = suite.run(Some(tests(n)), new Args(eventHandler)).succeeds()
      println(tests(n) + ": " + (if (isOk) "OK" else "Error"))
    }
    case _ => sys.exit(1)
  }
}