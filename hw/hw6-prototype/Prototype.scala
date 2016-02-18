// Homework idea:
// students will be provided all the code below. The goal is to make as
// many classes extend as many traits as possible (solutions are in comments).
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

// Solution: cannot extend T1
// Can extend T2[Int, Int, String, String]
class C1 {
  def f(a: Int, b: Int): Int = x + y
  def g(c: String): String = c + " hai"
  def h(d: String): Int = d.length
}


// Solution:
// Can extend T1[Int, Int] and T2[Int, Int, Int, Int]
class C2 {
  def f(a: Int, b: Int): Int = x + y
  def g(c: Int):  Int = c + 1
  def h(d: Int): Int = d.length
}

// Can extend T2[Int, A, A, String]
// Cannot extend T1
class C3[A](x: A) {
  def f(a: Int, b: A) = x
  def g(c: A): String = "hello"
  def h(d: String): A = x
}
