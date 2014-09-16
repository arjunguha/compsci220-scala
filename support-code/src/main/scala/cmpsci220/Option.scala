package cmpsci220

/**
 * @group Option
 */
sealed trait Option[A]

/**
 * @group Option
 */
case class Some[A](value: A) extends Option[A]

/**
 * @group Option
 */
case class None[A]() extends Option[A]