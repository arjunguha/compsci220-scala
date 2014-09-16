import org.scalatest.FunSuite
import cmpsci220._

class ListSuite extends FunSuite {

  test("lists should pretty-print") {
    assert(Cons(1, Cons(2, Cons(3, Empty()))).toString == "List(1, 2, 3)")
  }

  test("List shorthand works") {
    assert(Cons(1, Cons(2, Cons(3, Empty()))) == List(1, 2, 3))
  }

  test("reverse works") {
    assert(reverse(List(1,2,3)) == List(3, 2, 1))
  }

  test("map works") {
    assert(map((n: Int) => n + 1, List(1, 2, 3)) == List(2, 3, 4))
  }

}