import cmpsci220._

var success = true
test("this test should be disabled.") {
  println("Test entered. Program will assert(false).")
  success = false
}

assert(success)