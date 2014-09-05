package cmpsci220.hw.pong

import math._

case class Vector2D(x: Double, y: Double) {


  def add(other: Vector2D): Vector2D = {
    Vector2D(this.x + other.x, this.y + other.y)
  }

  def +(other: Vector2D): Vector2D = this.add(other)

  def unary_- = Vector2D(-x, -y)

  /** The distance between two points */
  def distance(other: Vector2D): Double = {
    val p1 = this
    val p2 = other
    sqrt(pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2))
  }

  /** The interior angle, in radians, between the line from this point to
   * {@code a} and this point to {@code b}
   */
  def angle(a: Vector2D, b: Vector2D): Double = {
    val c = this
    val ac = c.distance(a)
    val bc = c.distance(b)
    val ab = a.distance(b)
    acos((ac * ac + bc * bc - ab * ab) / (2 * ac * bc))
  }

  /** The distance this point and the line segment {@code p1}-{@code p2}
   */
  def distanceToLineSegment(p1: Vector2D, p2: Vector2D): Double = {
    val q = this
    val angle = q.angle(p1, p2)
    if (angle >= Pi/2) {
      // the side p1 -- p2 is the hypotenuse
      p1.distance(q) * sin(p1.angle(q, p2))
    }
    else {
      // Either p1 -- q or p2 -- q is the hypotenuse
      min(q.distance(p1), q.distance(p2))
    }
  }


}
