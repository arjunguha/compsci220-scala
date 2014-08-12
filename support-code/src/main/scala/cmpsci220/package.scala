package object cmpsci220 {

  /** This value enables all test cases */
  implicit val testsEnabled = TestsEnabled(true)

  val error = sys.error _

  /** A test case */
  def test(description : String)(body : => Unit)
    (implicit testsEnabled : TestsEnabled) : Unit = {
    if (testsEnabled.isEnabled) {
      try {
        body
        println(s"Succeeded $description")
      }
      catch {
        case (exn : Throwable) => println(s"Failed $description $exn")
      }
    }
  }

  def fails(description : String)(body : => Unit)
    (implicit testsEnabled : TestsEnabled) : Unit = {
    if (testsEnabled.isEnabled) {
      try {
        body
        println(s"Failed $description (expected error)")
      }
      catch {
        case (exn : Throwable) => {
          println(s"Succeeded $description (${exn.getMessage()})")
        }
      }
    }
  }


}