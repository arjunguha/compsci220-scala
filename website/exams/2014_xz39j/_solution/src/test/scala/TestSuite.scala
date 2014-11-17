
import Solution._

class TestSuite extends org.scalatest.FunSuite {

  test("Does fringe evaluate the whole tree?") {

    val t1 = fringe(Node(Leaf(1), Node(Leaf(2), Leaf(3))))
    assert(t1.hasDefiniteSize == false)

  }

  test("Does sameFringe evaluate the whole tree when different?") {

    val t1 = fringe(Node(Leaf(1), Node(Leaf(2), Leaf(3))))
    val t2 = fringe(Node(Node(Leaf(1), Leaf(2)), Leaf(4)))

    assert(!sameFringe(t1, t2))
    assert(t1.hasDefiniteSize == false)
    assert(t2.hasDefiniteSize == false)

  }

  test("Does sameFringe evaluate the whole tree when same?") {

    val t1 = fringe(Node(Leaf(1), Node(Leaf(2), Leaf(3))))
    val t2 = fringe(Node(Node(Leaf(1), Leaf(2)), Leaf(3)))

    assert(sameFringe(t1, t2))
    assert(t1.hasDefiniteSize)
    assert(t2.hasDefiniteSize)
  }



}