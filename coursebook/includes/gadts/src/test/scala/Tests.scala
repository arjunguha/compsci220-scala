class Tests extends org.scalatest.FunSuite {

  import compact._

  test("set-get in first byte") {
    val arr = BitArray(32)
    arr.set(0, true)
    assert(arr.get(0) == true)
    arr.set(10, true)
    assert(arr.get(10) == true)
    arr.set(10, false)
    assert(arr.get(10) == false)
    arr.set(11, true)
    assert(arr.get(11) == true)

  }

  test("set-get in second byte") {
    val arr = BitArray(64)
    arr.set(0, true)
    assert(arr.get(0) == true)
    arr.set(40, true)
    assert(arr.get(40) == true)
    arr.set(40, false)
    assert(arr.get(40) == false)
  }

}