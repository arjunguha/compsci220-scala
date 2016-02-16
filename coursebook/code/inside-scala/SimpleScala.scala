class SimpleScala {

  def factorial(n: Int, result: Int): Int = {
    if (n == 0) {
      result
    }
    else {
      factorial(n - 1, result * n)
    }
  }

}