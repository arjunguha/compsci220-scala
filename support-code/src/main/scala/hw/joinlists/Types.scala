package cmpsci220.hw.joinlists

sealed trait JoinList[A]
case class Empty[A]() extends JoinList[A]
case class Singleton[A](elt: A) extends JoinList[A]
case class Append[A](lst1: JoinList[A], lst2: JoinList[A]) extends JoinList[A]

/**
 * You need to implement these functions
 */
trait JoinListFunctions {

  /**
   * {@code max} returns the maximum value in the join-list.
   *
   * @param lst the join-list
   * @param compare a comparator for the type A. If the comparator returns
   *                {@code true}, the first argument to the comparator is
   *                greater than the second argument.
   * @returns the maximum value in the join-list. If there are several
   *          maximum values, any of them may be produced.
   * @throws an exception if the join-list is empty
   */
  def max[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A]

  /**
   * {@code length} returns the length of the join-list
   */

  def length[A](lst: JoinList[A]): Int

  /**
   * {@code first} returns the first element of the join-list
   *
   * @throws an exception if the join-list is empty
   */
  def first[A](lst: JoinList[A]): A

  /**
   * {@code rest} returns a list containing all elements but the first of a
   * non-empty list
   *
   * @throws an exception if the join-list is empty
   */
  def rest[A](lst: JoinList[A]): JoinList[A]

  /**
   * {@code nth} returns the nth element (using a 0 based index) of a join-list
   * containing at least n elements. For example,
   * {@code nth(lst, 0) == first(lst)}
   *
   * @throws an exception if the join-list is empty
   */
  def nth[A](lst: JoinList[A], i: Int): A

  /**
   * {@code map} applies an operator to each element of a join-list and returns
   * the list of resulting values.
   *
   * {@code map} must not change the shape of the join-list. For example, it
   * must be that {@code map(id, lst) == lst}.
   */
  def map[A,B](f: A => B, lst: JoinList[A]): JoinList[B]

  /**
   * {@code filter}  applies a predicate to each element of a list and returns
   * the list of elements for which the operator returned true.
   */
  def filter[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A]

  /**
   * {@code reduce} distributes an operator across a non-empty list. That is,
   * given the list of elements e1, e2, ..., e n, and the operator op, j-reduce computes the equivalent of e1 op e2 op ... op e n.
   * For instance,
   *
   * (j-reduce + (list->join-list (list 1 2 3)))
   *   => 6
   *
   * (j-reduce max (list->join-list (list 3 1 4 6 2)))
   *   => 6
   */
  def reduce[A](op: (A, A) => A, lst: JoinList[A]): A


}