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
  
  def fold[A,B](acc: B, f: (B,A) => B, xs: List[A]) : B = {
    xs match {
      case Nil => acc
      case head :: tail => fold(f(acc, head), f, tail)
    }
  }
  
  def exercise1(xs: List[Bird]) : List[String] = {
    val f = (x: Bird) => x match {
      case Duck() => "dog food"
      case Goose() => "pate"
    }
    
    map(f, xs)
  }
  
  def exercise3(xs: List[Bird]) : Int = {
    val f = (acc: Int, x: Bird) => x match {
      case Duck() => acc + 1
      case Goose() => acc + 10
    }
    
    fold(0, f, xs)
  }
}