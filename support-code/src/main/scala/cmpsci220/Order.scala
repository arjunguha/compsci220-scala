package cmpsci220

sealed trait Order
case class LT() extends Order
case class GT() extends Order
case class EQ() extends Order


object Order {

  def compareInt(x : Int, y : Int) : Order = {
    if (x < y) LT()
    else if (x > y) GT()
    else EQ()
  }

}