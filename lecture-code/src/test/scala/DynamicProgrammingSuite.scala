import lectures.dynamicprogramming._

import org.scalatest._

class DynamicProgrammingSuite extends FunSuite {

  test("substring finds a string in the middle of a list") {
    import Substring._
    assert(substring("jun Gu", "Arjun Guha"))
  }

  test("substring - negative example") {
    import Substring._
    assert(substring("juju", "Arjun Guha") == false)
  }

  test("recursive LCS easy") {
    import RecursiveLCS._
    counter = 0
    assert(lcs("abc".toList, "casbsac".toList) == "abc".toList)
    info(s"$counter recursive calls")
  }

  test("recursive LCS - 2") {
    import RecursiveLCS._
    counter = 0
    assert(lcs("bc".toList, "ccassac".toList) == "c".toList)
    info(s"$counter recursive calls")
  }

  test("recursive LCS - long") {
    import RecursiveLCS._
    counter = 0
    assert(lcs("axybczwfdgh".toList, "123a921b2c212d".toList) == "abcd".toList)
    info(s"$counter recursive calls")
  }

  test("recursive LCS length - long") {
    import RecursiveLCSLength._
    counter = 0
    assert(lcsLen("axybczwfdgh".toArray, "123a921b2c212d".toArray) == 4)
    info(s"$counter recursive calls")
  }

  test("dynamic programming LCS length - short") {
    import DynamicProgrammingLCSLength._
    counter = 0
    assert(lcsLen("bc".toArray, "ccassac".toArray) == 1)
    info(s"$counter recursive calls")
  }

  test("dynamic programming LCS length - long") {
    import DynamicProgrammingLCSLength._
    counter = 0
    assert(lcsLen("axybczwfdgh".toArray, "123a921b2c212d".toArray) == 4)
    info(s"$counter recursive calls")
  }
}

