object HigherOrder {


  def f(x: Int) = x + 10

  def g(h: Int => Int, y: Int) = h(y)

  val result = g(f, 10)

}