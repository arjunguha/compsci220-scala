class Tests extends org.scalatest.FunSuite {

  import Implicits._

  test("simple alternation and sequencing") {
    val re = "cs2" >> ("2" | "3") >> "0"
    assert(re.contains("cs220"))
    assert(re.contains("cs230"))
    assert(re.contains("cs240") == false)
    assert(re.contains("cs220x") == false)
  }

  test("iteration") {
    val re = "a".star >> "b".star
    assert(re.contains("aaaa"))
    assert(re.contains("bbbb"))
    assert(re.contains("aaaaaabbbbbb"))
    assert(re.contains(""))
    assert(re.contains("aaaabbbbaaa") == false)
  }

  test("iteration with memoization") {
    val re = new MemoizingMatcher("a".star >> "b".star)
    assert(re.contains("aabb"))
    val x = re.size
    assert(re.contains("bbbb"))
    assert(re.contains("aaaaaabbbbbb"))
    assert(re.size == x, "no more states were added")
    assert(re.contains("aaaabbbbaaa") == false)
    val y = re.size
    assert (y > x, "we explored a state that leads to failure")
    assert(re.contains("aaaabbbbaaaaaaa") == false)
    assert (re.size == y, "no more states were added")
  }

}