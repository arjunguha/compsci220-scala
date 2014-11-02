package cmpsci220.regex

class Memoize[A,B](f: A => B) {

  val tbl = collection.mutable.Map[A,B]()

  def apply(x: A): B = {
    tbl.get(x) match {
      case None => {
        val y = f(x)
        tbl += (x -> f(x))
        y
      }
      case Some(y) => y
    }
  }
}
