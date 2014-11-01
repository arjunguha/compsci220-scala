import cmpsci220.regex._
import Dsl._

class RegexSuite extends org.scalatest.FunSuite {

  test("characters are implicitly converted, alternation works") {
    val re: Regex = 'a' <||> 'b'
    assert(re == Alt(Character('a'), Character('b')))
  }

  test("strings are implicitly converted") {
    val re: Regex = "arjun"
  }

  test("rich strings as regular expressions") {
    val re: Regex = "arjun" <||> "guha"
  }

  test("postfix star works") {
    import scala.language.postfixOps
    val re: Regex = Character('x')<*>
  }

  test("postfix star works on strings") {
    import scala.language.postfixOps
    val re: Regex = "hello"<*>
  }

  test("deriv test 1") {
    import scala.language.postfixOps
    val re: Regex  = "abab".<*>
    assert(Derivative.matches(re, "abababab".toList))
  }

}
