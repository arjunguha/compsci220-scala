package hw.streams


trait Generator[T] {
  def next(): (T, Generator[T])
}

trait SolutionLike {

  def from(x: Int): Generator[Int]
  def map[A,B](f: A => B, agen: Generator[A]): Generator[B]
  val pow: Generator[Int]
  def nth[A](agen: Generator[A], index: Int): A
  def filter[A](pred: (A) => Boolean, agen: Generator[A]): Generator[A]
  def interleave[A](agen1: Generator[A], agen2: Generator[A]): Generator[A]
  def sift(n: Int, agen: Generator[Int]): Generator[Int]
  val prime: Generator[Int]
  def total(agen: Generator[Double]): Generator[Double]
}