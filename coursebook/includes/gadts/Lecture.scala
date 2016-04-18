package compact {

  trait CompactArray[T] extends Any {
    def get(index: Int): T
    def set(index: Int, value: T): Unit
    def length(): Int
  }

  case class AnyArray[T](elts: Array[T]) extends AnyVal with CompactArray[T] {
    def get(index: Int): T = elts(index)
    def set(index: Int, value: T): Unit = elts(index) = value
    def length() = elts.length
  }

  case class BitArray(elts: Array[Int]) extends AnyVal with CompactArray[Boolean] {
    def get(index: Int): Boolean = elts(index >> 5) >> (index & 0x1F) == 1

    def set(index: Int, value: Boolean) = {

      if (value) {
        elts(index >> 5) = elts(index >> 5) | (1 << (index & 0x1F))
      }
      else {
        elts(index >> 5) = elts(index >> 5) & ~(1 << (index & 0x1F))
      }
    }

    def length() = elts.length * 32
  }

  object BitArray {

    def apply(size: Int) = {
      assert(size % 32 == 0)
      new BitArray(Array.fill(size >> 5)(0))
    }
  }


}