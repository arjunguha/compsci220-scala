package hw.measurement

sealed trait Order
case class LT() extends Order
case class GT() extends Order
case class EQ() extends Order
