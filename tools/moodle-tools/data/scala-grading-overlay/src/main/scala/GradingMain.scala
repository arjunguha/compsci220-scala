object GradingMain extends App {
  import org.scalatest.Args
  import org.scalatest.events.Event
  val suite = new GradingTests()

  def eventHandler(event: Event): Unit = {
  }

  args.toList match {
    case List("run-test", name) => {
      val isOk = suite.run(Some(name), new Args(eventHandler)).succeeds()
      println(name + ": " + (if (isOk) "OK" else "Error"))
    }
    case _ => sys.exit(1)
  }
}