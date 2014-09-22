package cmpsci220.hw.measurement
import cmpsci220._

trait MeasurementFunctions {

  def time[A, B](f : A => B, a : A) : Long

  def averageTime[A, B](n: Int, f: A => B, a: A) : Double

  def revOrder(n : Int) : List[Int]

  def randomInts(n : Int) : List[Int]

  def insertAllOrdList(values: List[Int]): OrdList

  def insertAllBST(values: List[Int]): BST

  def insertAllAVL(values: List[Int]): AVL

  def isMemberAllOrdList(values: List[Int], lst: OrdList) : Boolean

  def isMemberAllBST(values: List[Int], bst: BST) : Boolean

  def isMemberAllAVL(values: List[Int], avl: AVL) : Boolean

  def insertAll[T](empty : T, insert : (Int, T) => T, values: List[Int]): T

  def isMemberAll[T](isMember : (Int, T) => Boolean,
                     values: List[Int],
                     container: T): Boolean

}