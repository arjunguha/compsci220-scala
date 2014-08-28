import cmpsci220._
import cmpsci220.hw.measurement._

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

def averageTime[A, B](trials : Int, f : A => B, a : A) : Double =
  totalTime(trials, f, a) / trials

def iotaHelper(start : Int, stop : Int) : List[Int] =
  if (start > stop) Empty()
  else Cons(start, iotaHelper(start + 1, stop))

def iota(n : Int) : List[Int] = iotaHelper(0, n)

def randomInts(n : Int) : List[Int] =
  if (n == 0) Empty()
  else Cons(util.Random.nextInt(), randomInts(n - 1))

def insertAll[T](empty : T, insert : (Int, T) => T, values : List[Int]) : T =
  values match {
    case Empty() => empty
    case Cons(n, rest) => insert(n, insertAll(empty, insert, rest))
  }

def isMemberAll[T](isMember : (Int, T) => Boolean, values : List[Int], set : T) : Boolean =
  values match {
    case Empty() => true
    case Cons(n, rest) => isMember(n, set) && isMemberAll(isMember, rest, set)
  }

def generateRandomData(sizes : List[Int]) : List[(Int, List[Int])] = sizes match {
  case Empty() => Empty()
  case Cons(n, rest) => Cons((n, randomInts(n)), generateRandomData(rest))
}

def insertAllOrdList(values : List[Int]) : List[Int] =
  insertAll(Empty[Int](), (n : Int, lst : List[Int]) => OrdList.insert(Order.compareInt, n, lst), values)

def insertAllBST(values: List[Int]): BinTree[Int] =
  insertAll(Leaf[Int](), (n: Int, tree: BinTree[Int]) => BST.insert(Order.compareInt, n, tree), values)

def benchmarkInsertAll[A](insertAll: (List[Int]) => A, lst : List[(Int, List[Int])]) : List[(Int, Double)] =
  lst match {
    case Empty() => Empty()
    case Cons((len, data), rest) => {
      val avgTime = averageTime(10, insertAll, data)
      Cons((len, avgTime), benchmarkInsertAll(insertAll, rest))
    }
  }


val data =  generateRandomData(Cons(500, Cons(1000, Cons(2000, Cons(4000, Empty())))))

println(benchmarkInsertAll(insertAllOrdList, data))
println(benchmarkInsertAll(insertAllBST, data))