import org.scalatest._
import cmpsci220.regex._
import Dsl._

class RegexSuite extends FunSuite with prop.GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig =
    PropertyCheckConfig(minSize = 2, maxSize = 10)

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

  test("counterexample 1") {
    assert(Derivative.matches(Alt(Character('f'),Character('f')),
                              List('f')))
  }

  test("counterexample 2") {
    val re = Star(Star('f'))
    assert(Regex.matches(re, "f".toList))
  }


  test("testing derivative-based matching") {
    forAll(GenRegex.genRegex) { (re: Regex) =>
      for (w <- Words.words(re).take(10)) {
        assert(Derivative.matches(re, w), s"${w.mkString} should be in $re")
      }
    }
  }

  test("testing naive matching") {
    forAll(GenRegex.genRegex) { (re: Regex) =>
      for (w <- Words.words(re).take(10)) {
        println(w)
        assert(Regex.matches(re, w), s"${w.mkString} should be in $re")
      }
    }
  }

  test("regex words test") {
    val re: Regex = Seq("cs2", Seq('2' <||> '3', '0'))
    assert(Words.words(re).length == 2)
  }

}
