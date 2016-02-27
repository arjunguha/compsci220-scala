object ImplicitsLecture {

  class Vector2D(x: Double, y: Double) {
    def add(other: Vector2D): Vector2D = new Vector2D(this.x + other.x, this.y + other.y)
    def mul(factor: Double): Vector2D = new Vector2D(factor * x, factor * y)
    def neg(): Vector = Vector2D(-x, -y)
  }

  val v1 = new Vector2D(2, 3)
  val v2 = v1.add(v1) * 3

}
