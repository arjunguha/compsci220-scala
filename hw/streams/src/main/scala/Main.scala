import hw.streams._

object Main extends SolutionLike {

  // Provided
  def cons[A](head: A, tail: =>Generator[A]): Generator[A] = new Generator[A] {
    def next() = (head, tail)
  }

  // Q1
  def from(x: Int): Generator[Int] = cons(x, from(x + 1))

  
  // Q2
  def map[A,B](f: A => B, agen: Generator[A]): Generator[B] = {
    val (x, agenNext) = agen.next()
    cons(f(x), map(f, agenNext))
  }

  // Q3. Students must be told about math.pow and the .toInt method
  val pow = map((n: Int) => math.pow(2, n).toInt, from(0))

  // Q4
  def nth[A](agen: Generator[A], index: Int): A = {
    val (x, nextGen) = agen.next()
    if (index == 0) x else nth(nextGen, index - 1)
  }

  // Q5
  def filter[A](pred: (A) => Boolean, agen: Generator[A]): Generator[A] = {
    val (x, genNext) = agen.next()
    if (pred(x)) 
      cons(x, filter(pred, genNext))
    else
      filter(pred, genNext)
  }

  // Q6
  def interleave[A](agen1: Generator[A], agen2: Generator[A]): Generator[A] = {
    val (x, agen1Next) = agen1.next()
    val (y, agen2Next) = agen2.next()
    cons(x, cons(y, interleave(agen1Next, agen2Next)))
  }

  // helper for Q7
  def totalHelper(sum: Double, agen: Generator[Double]): Generator[Double] = {
    val (head, tail) = agen.next()
    cons(sum + head, totalHelper(sum + head, tail))
  }

  // Q7
  def total(agen: Generator[Double]): Generator[Double] = totalHelper(0, agen)

  // Q8: hint use filter
  def sift(n: Int, agen: Generator[Int]): Generator[Int] = {
    filter((m: Int) => m % n != 0, agen)
  }
  
  // Helper for Q9
  def makePrimes(astream: Generator[Int]): Generator[Int] = {
    val (head, tail) = astream.next()
    cons(head, makePrimes(sift(head, tail)))
  }

  // Q9
  val prime = makePrimes(from(2))

}