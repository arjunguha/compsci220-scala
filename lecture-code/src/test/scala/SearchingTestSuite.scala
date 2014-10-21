import lectures.searching._
import Searching._
import org.scalatest._

class SearchingTestSuite extends FunSuite {

  test("substring finds a string in the middle of a list") {
    assert(substring("jun Gu", "Arjun Guha"))
  }

  test("substring - negative example") {
    assert(substring("juju", "Arjun Guha") == false)
  }

  test("subpaths on a tree") {
    val tr = Node(Node(Leaf, "ball", Leaf), "car", Node(Leaf, "bus", Leaf))
    assert(subPath(List("car", "bus"), tr))
    assert(subPath(List("car", "ball"), tr))
    assert(subPath(List("bus"), tr))
    assert(!subPath(List("ball", "bus"), tr))
  }

  test("subpaths on a rose tree") {
    val leaf = RoseTree[String](Map())
    val tr = RoseTree(Map("ball" -> RoseTree(Map("car" -> RoseTree(Map("cat" -> leaf)),
                                                 "baloon" -> leaf)),
                          "baloon" -> RoseTree(Map("house" -> leaf))))
    assert(subPath(List("car", "cat"), tr))
  }

}

