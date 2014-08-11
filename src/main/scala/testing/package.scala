package cmpsci220

package object testing {

  val error = sys.error _

  /** A test case */
  def test(description : String)(body : => Unit) : Unit = {
    try {
      body
      println(s"Succeeded $description")
    }
    catch {
      case (exn : Throwable) => println(s"Failed $description $exn")
    }
  }

  // TODO(arjun): check that error message matches description
  def fails(description : String)(body : => Unit) : Unit = {
    try {
      body
      println(s"Failed $description (expected error)")
    }
    catch {
      case (exn : Throwable) => println(s"Succeeded $description (${exn.getMessage()})")
    }
  }

}

package testing {

  /** Needed to force Scaladoc to generate the index (on the left-hand side).
   * Remove when we have a reasonable class here.
   */
  class Dummy

}