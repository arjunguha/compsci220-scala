package hw.combinator
/**
 * Provides the [[JsonProc]] class that is used in the json combinator
 * assignment.
 */


/**
 * Generic class that implements a processor. A function passed into a [[JsonProc]]
 * can be thought of as a suspended computation that can be run in future.
 *
 * A simple example of this class. Function f takes a string and splits it into
 * a list.
 * {{{
 * def f(str: String): List[Char] = str.split("").toList
 * def proc = JsonProc[String, Char](str)
 * }}}
 *
 * To "run" the processor:
 * {{{
 * val output = proc.func("abc") // returns List('a', 'b', 'c')
 * }}}
 *
 * @tparam S The type of the input value.
 * @tparam T The type of the output value.
 * @param func A function that takes values of type S and returns a list of
 *             value of type T
 * @return A [[JsonProc]] representing the suspended computation of [[func]].
 *
 */
case class JsonProc[S, T](val func: S => List[T]) {

  /**
   * Method that allows two [[JsonProc]] values to be composed with each other.
   * This is used as an infix operator.
   *
   * For example:
   * {{{
   * def splitString(str: String): List[Char] = str.split("").toList
   * def splitStringProc = JsonProc[String, Char](str)
   *
   * def upperCase(ch: Char): List[Char] = List(ch.toUpperCase)
   * def upperCaseProc = JsonProc[Char, Char](ch)
   *
   * def upperCaseAllChars = splitStringProc >>> upperCaseProc
   * }}}
   *
   * When we "run" the combined process:
   *
   * {{{
   * val output = upperCaseAllChars.func("abc") // returns List('A', 'B', 'C')
   * }}}
   *
   * @param other The [[JsonProc]] that should be run after this one.
   * @tparam T The type of the input value to other.
   * @tparam U The type of the output value to other.
   * @return A [[JsonProc]] that first runs this computation and then run the
   *         computation suspended by other on each output of this computation.
   */
  def >>>[U](other: JsonProc[T, U]) =
    new JsonProc[S, U](json => func(json).flatMap(other.func))

}

trait CombinatorLike {

  import hw.json.Json

  def addAll(json: Json): Double

  def key(k: String): JsonProc[Json,Json]
  def index(n: Int): JsonProc[Json,Json]
  def iter: JsonProc[Json,Json]
  def number: JsonProc[Json,Double]
  def recur: JsonProc[Json, Json]
  def split[S]: JsonProc[S, (S, S)]
  def swap[A, B]: JsonProc[(A, B), (B, A)]
  def first[A, B, C](proc: JsonProc[A, B]): JsonProc[(A, C), (B, C)]
  def second[A, B, C](proc: JsonProc[A, B]): JsonProc[(C, A), (C, B)]
  def combine[A]: JsonProc[(A, A), A]

}
