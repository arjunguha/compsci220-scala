package jshapes {
  trait Shape {
    def inShape(x: Double, y: Double): Boolean
  }

  class Circle(radius: Double) extends Shape {
     def inShape(x: Double, y: Double) = math.sqrt(x * x + y * y) <= radius
  }

  class Square(side: Double) extends Shape {
    def inShape(x: Double, y: Double) = x >= 0 && x <= side && y >= 0 && y <= side
  }
}

package jshapesext {

  import jshapes._

  trait Growable {
    def growShape(): Shape
  }

  class MyCircle(radius: Double) extends Circle(radius) with Growable {
    def growShape(): MyCircle = new MyCircle(radius * 2)
  }

  class MySquare(side: Double) extends Square(side) with Growable {
    def growShape(): MySquare = new MySquare(side * 2)
  }

  class Rectangle(width: Double, height: Double) extends Shape with Growable {
    def inShape(x: Double, y: Double): Boolean = x >= 0 && y >= 0 && x <= width && y <= height

    def growShape(): Rectangle = new Rectangle(width * 2, height * 2)
  }
}