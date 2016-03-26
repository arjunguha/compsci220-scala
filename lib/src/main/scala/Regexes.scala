package hw.regex

trait RegexLike {

  import scala.util.matching.Regex

  def notAlphanumeric: Regex
  def time: Regex
  def phone: Regex
  def zip: Regex
  def comment: Regex
  def numberPhrase: Regex
  def roman: Regex
  def date: Regex
  def evenParity: Regex
}