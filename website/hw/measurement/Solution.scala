import cmpsci220._
import cmpsci220.hw.measurement._

object Solution extends MeasurementFunctions {

  def time[A, B](f : A => B, a : A) : Long = {
    throw new UnsupportedOperationException("not implemented")
  }

  def averageTime[A, B](n: Int, f: A => B, a: A) : Double = {
    throw new UnsupportedOperationException("not implemented")
  }

  def revOrder(n : Int) : List[Int] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def randomInts(n : Int) : List[Int] = {
    throw new UnsupportedOperationException("not implemented")
  }

  def insertAllOrdList(values: List[Int]): OrdList = {
    throw new UnsupportedOperationException("not implemented")
  }

  def insertAllBST(values: List[Int]): BST = {
    throw new UnsupportedOperationException("not implemented")
  }

  def insertAllAVL(values: List[Int]): AVL = {
    throw new UnsupportedOperationException("not implemented")
  }

  def isMemberAllOrdList(values: List[Int], lst: OrdList) : Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

  def isMemberAllBST(values: List[Int], bst: BST) : Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

  def isMemberAllAVL(values: List[Int], avl: AVL) : Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

  def insertAll[T](empty : T, insert : (Int, T) => T, values: List[Int]): T = {
    throw new UnsupportedOperationException("not implemented")
  }

  def isMemberAll[T](isMember : (Int, T) => Boolean,
                     values: List[Int],
                     container: T): Boolean = {
    throw new UnsupportedOperationException("not implemented")
  }

}