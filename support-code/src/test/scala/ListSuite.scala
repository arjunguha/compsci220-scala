import org.scalatest.FunSuite
import cmpsci220.hw.recursion._

class ListSuite extends FunSuite {

  test("lists should pretty-print") {
    assert(Cons(1, Cons(2, Cons(3, Empty()))).toString == "List(1, 2, 3)")
  }

  test("List shorthand works") {
    assert(Cons(1, Cons(2, Cons(3, Empty()))) == List(1, 2, 3))
  }

}