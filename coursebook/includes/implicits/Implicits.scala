object Implicits {

  implicit class RichVector2D(vec: Vector2D) {
    def +(other: Vector2D): Vector2D = vec.add(other)
    def *(factor: Double): Vector2D = vec.mul(factor)
    def unary_- = vec.neg()
  }

  implicit def pointToVector2D(pt: (Int, Int)): Vector2D = new Vector2D(pt._1, pt._2)

}