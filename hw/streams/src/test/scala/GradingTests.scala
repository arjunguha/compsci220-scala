import Main._
import hw.streams._
class GradingTestsTests extends org.scalatest.FunSuite {

  // This is the solution to nth. In case students get this wrong, we use
  // this definition to test.
  def gradingNth[A](agen: Generator[A], index: Int): A = {
    val (x, nextGen) = agen.next()
    if (index == 0) x else gradingNth(nextGen, index - 1)
  }

  def gradingTake[A](agen: Generator[A], len: Int): List[A] = {
    if (len == 0) {
      Nil
    }
    else {
      val (x, agenNext) = agen.next()
      x :: gradingTake(agenNext, len - 1)
    }
  }

  def gradingFrom(x: Int): Generator[Int] = cons(x, gradingFrom(x + 1))

  def gradingFromFloat(x: Double): Generator[Double] = {
    cons(x, gradingFromFloat(x + 1))
  }


  test("The 0th number in from(0) is 0") {
    val nums = from(0)
    assert(gradingNth(nums, 0) == 0)
  }

  test("The 5th number (counting from zero) in from(0) is 5") {
    val nums = from(0)
    assert(gradingNth(nums, 5) == 5)
  }

  test("The 3rd number (counting from zero) in from(10) is 13") {
    assert(gradingNth(from(10), 3) == 13)
  }

  test("map((x: Int) => x * 2, from(0)) produces 0, 2, 4, 6, 8, ...") {
    assert(gradingTake(map((x: Int) => x * 2, gradingFrom(0)), 5) ==
      List(0, 2, 4, 6, 8))
  }

  test("map((x: Int) => x.toString, from(0)) produces strings") {
    assert(gradingTake(map((x: Int) => x.toString, gradingFrom(0)), 5) ==
      List("0", "1", "2", "3", "4"))
  }

  test("pow produces 1, 2, 4, 8, ...") {
    assert(gradingTake(pow, 5) == List(1, 2, 4, 8, 16))
  }

  test("nth(x, 1) produces the first/second value of the stream") {
    val r = nth(gradingFrom(10), 1)
    assert(r == 10 || r == 11)
  }

  test("nth(x, 10) produces the 11th/10th value of the stream") {
    val r = nth(gradingFrom(10), 10)
    assert(r == 10 || r == 9)
  }

  test("filter(isEven, from(0)) produces only even numbers") {
    assert(gradingTake(filter((x: Int) => x % 2 == 0, gradingFrom(0)), 5) ==
      List(0, 2, 4, 6, 8))
  }

  test("filter(isOdd, from(0)) produces only odd numbers") {
    assert(gradingTake(filter((x: Int) => x % 2 == 1, gradingFrom(0)), 5) ==
      List(1, 3, 5, 7, 9))
  }

  test("filter(x == 2, from(0)) produces 2") {
    val gen = filter((x: Int) => x == 2, gradingFrom(0))
    val (x, _) = gen.next()
    assert(x == 2)
  }

  test("sift(2, from(0)) produces only odd numbers") {
    assert(gradingTake(sift(2, gradingFrom(0)), 5) ==
      List(1, 3, 5, 7, 9))
  }

  test("sift(4, from(0)) removes multiples of 4") {
    assert(gradingTake(sift(4, gradingFrom(0)), 8) ==
      List(1, 2, 3, 5, 6, 7, 9, 10))
  }

  test("prime starts with 2") {
    assert(gradingNth(prime, 0) == 2)
  }

  test("the first few primes are 2, 3, 5, 7, 11, ...") {
    assert(gradingTake(prime, 5) == List(2, 3, 5, 7, 11))
  }

  test("the running total of from(0) is 0, 1, 3, 6, 10 ...") {
    val astream = total(gradingFromFloat(0))
    assert(gradingTake(astream, 5) ==  List(0, 1, 3, 6, 10))
  }

}
