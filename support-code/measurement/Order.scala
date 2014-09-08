package cmpsci220

/**
 * @group Ordering
 */
sealed trait Order

/**
 * @group Ordering
 */
case class LT() extends Order

/**
 * @group Ordering
 */
case class GT() extends Order

/**
 * @group Ordering
 */
case class EQ() extends Order

/**
 * @group Ordering
 */
object Order {

  def compareInt(x : Int, y : Int) : Order = {
    if (x < y) LT()
    else if (x > y) GT()
    else EQ()
  }

}