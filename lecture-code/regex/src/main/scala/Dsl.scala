package cmpsci220.regex

import scala.language.{implicitConversions, postfixOps}


object Dsl {

  implicit class RichRegex(regex: Regex) {
    def <||>(other: Regex): Regex = Alt(regex, other)
    def <*>(): Regex = Star(regex)
  }

  implicit class RichChar(ch: Char) {
    def <||>(other: Regex) = Alt(Character(ch), other)
  }

  implicit class RichString(str: String) {
    def <||>(other: Regex) = Alt(stringToRegex(str), other)
    def <*>(): Regex = Star(stringToRegex(str))
  }

  implicit def charToRegex(ch: Char): Regex = Character(ch)

  implicit def stringToRegex(str: String): Regex = {
    str.toList.foldRight(One : Regex) { (ch, re) => Seq(Character(ch), re) }
  }

}