trait ArrayLike[T] extends Any {
  def get(index: Int): T
  def set(index: Int, value: T): Unit
  def length(): Int
}

case class BitArray(elts: Array[Int]) extends AnyVal with ArrayLike[Boolean] {
  def length(): Int = elts.length *  32

  def get(index: Int): Boolean = elts(index >> 5) >> (index & 0x1F) == 1

  def set(index: Int, value: Boolean) = {
    if (value) {
      elts(index >> 5) = elts(index >> 5) | (1 << (index & 0x1F))
    }
    else {
      elts(index >> 5) = elts(index >> 5) & ~(1 << (index & 0x1F))
    }
  }
}

case class AnyArray[T](elts: Array[T]) extends AnyVal with ArrayLike[T] {
  def length(): Int = elts.length

  def get(index: Int): T = elts(index)

  def set(index: Int, value: T): Unit = elts(index) = value
}