object Exercises {
  sealed trait Bird
  case class Duck() extends Bird
  case class Goose() extends Bird
  
  def map[A,B](f: A => B, xs: List[A]) : List[B] = {
    xs match {
      case head :: tail => f(head) :: map(f, tail)
      case Nil => Nil
    }
  }
  
  def exercise1(xs: List[Bird]) : List[String] = {
    val f = (x: Bird) => x match {
      case Duck() => "dog food"
      case Goose() => "pate"
    }
    
    map(f, xs)
  }
}