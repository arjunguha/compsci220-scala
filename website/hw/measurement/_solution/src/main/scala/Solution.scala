import cmpsci220._
import cmpsci220.hw.measurement._

object Solution extends MeasurementFunctions {
  def time[A, B](f : A => B, a : A) : Long = {
    val start = System.currentTimeMillis()
    val b = f(a)
    val end = System.currentTimeMillis()
    return end - start
  }

  def totalTime[A, B](trials : Int, f : A => B, a : A) : Long = {
    if (trials == 0)
      0
    else
     time(f, a) + totalTime(trials - 1, f, a)
  }

  def trials[A, B](n : Int, f : A => B, a : A) : List[Double] = {
    if (n == 0)
      Empty()
    else
     Cons(time(f, a).toDouble, trials(n - 1, f, a))
  }

  def averageTime[A, B](trials : Int, f : A => B, a : A) : Double =
    totalTime(trials, f, a) / trials


  def revOrder(n : Int) : List[Int] = n match {
    case 0 => Empty()
    case n => Cons(n, revOrder(n - 1))
  }

  def randomInts(n : Int) : List[Int] = {
    if (n == 0) Empty()
    else Cons(util.Random.nextInt(), randomInts(n - 1))
  }

  def insertAll[T](empty : T, insert : (Int, T) => T, values : List[Int]) : T =
    values match {
      case Empty() => empty
      case Cons(value, rest) => insert(value, insertAll(insert(value, empty), insert, rest))
    }

  def isMemberAll[T](isMember : (Int, T) => Boolean, values: List[Int], container: T): Boolean =
    values match {
      case Empty() => true
      case Cons(v, rest) => isMember(v, container)  && isMemberAll(isMember, rest, container)
    }

  def insertAllOrdList(values: List[Int]): OrdList =
    insertAll(emptyOrdList, insertOrdList, values)

  def insertAllAVL(values: List[Int]): AVL =
    insertAll(emptyAVL, insertAVL, values)

  def insertAllBST(values: List[Int]): BST =
    insertAll(emptyBST, insertBST, values)


  def isMemberAllOrdList(values: List[Int], lst: OrdList) : Boolean = {
    isMemberAll(isMemberOrdList, values, lst)
  }

  def isMemberAllBST(values: List[Int], bst: BST) : Boolean = {
    isMemberAll(isMemberBST, values, bst)
  }

  def isMemberAllAVL(values: List[Int], avl: AVL) : Boolean = {
    isMemberAll(isMemberAVL, values, avl)
  }

  def append[A](lst1: List[A], lst2: List[A]): List[A] = lst1 match {
    case Empty() => lst2
    case Cons(x, xs) => Cons(x, append(xs, lst2))
  }

  def concatMap[A,B](f: A => List[B], lst: List[A]): List[B] = lst match {
    case Empty() => Empty()
    case Cons(x, xs) => append(f(x), concatMap(f, xs))
  }


  /**
   * Calculates the average time needed to insert values into the set
   *
   * @param insertAll a function that inserts a list of values into an empty set
   * @param trials the number of trials to run
   * @param values the list of values to insert
   * @returns A pair (n, t), where n is the number of values and t is the
   *          average time needed to insert all values, divided by the
   *          number of values
   */
  def timeInsertAll[A](insertAll: List[Int] => A, trials: Int)(values: List[Int]): (Double, Double) = {
    val n = length(values).toDouble
    val t = averageTime(trials, insertAll, values)
    (n, t / n)
  }

}