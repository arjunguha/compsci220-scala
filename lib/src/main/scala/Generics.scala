package hw.generics

/**
 * A trait that represents how two values are ordered.
 */
trait Ordering
case object EQ extends Ordering
case object LT extends Ordering
case object GT extends Ordering

/** A trait for ordered values. */
trait Ordered[A] {

  /** Compare this object to the {@code other} object.
   *
   * If {@code this.compare(other)} produces:
   * - [[EQ]], then the two objects are equivalent
   * - [[LT]], then {@code this} is less-than {@code other}
   * - [[GT]], then {@code this} is greater-than {@code other}
   */
  def compare(other: A): Ordering
}

/**
 * A trait for list-like collections, where {@code E} is the type of the element
 * and {@code L} is the type of the list-like collection.
 */
trait ListLike[E, L] {
  /** Produces {@code true} if this object represents an empty list. */
  def isEmpty(): Boolean

  /** Produces the head of this list.
   *
   * @throws java.lang.IllegalArgumentException If this list is empty.
   */
  def head(): E

  /** Produces the tail of this list.
   *
   * @throws java.lang.IllegalArgumentException If this list is empty.
   */
  def tail(): L

  /** Produces a new list with {@code head} as the head and this object as
   *  the tail.
   */
  def cons(head: E): L
}

/**
 * A linked list that implements the [[ListLike]] trait.
 */
sealed trait MyList[A] extends ListLike[A, MyList[A]]

case class Cons[A](hd: A, tl: MyList[A]) extends MyList[A] {
  def isEmpty(): Boolean = false
  def head() = hd
  def tail() = tl
  def cons(newHd: A) = new Cons(newHd, this)
}

case class Empty[A]() extends MyList[A] {
  def isEmpty(): Boolean = true
  def head() = throw new Exception("empty list")
  def tail() = throw new Exception("empty list")
  def cons(newHd: A) = new Cons(newHd, this)
}

trait T1[X, Y] {
  def f(a: X, b: Y): Y
  def g(c: X): Y
  def h(d: Y): X
}

trait T2[X, Y, Z, W] {
  def f(a: X, b: Y): Y
  def g(c: Z): W
  def h(d: W): Y
}

trait T3[A, B, C, D, E, F, G] {
  def f(a: A, b: B): C
  def g(c: D): E
  def h(d: F): G
}