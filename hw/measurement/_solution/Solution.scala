import hw.measurement._

  def compareInt(x : Int, y : Int) : Order = {
    if (x < y) LT()
    else if (x > y) GT()
    else EQ()
  }

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
  if (start > stop) Nil()
  else Cons(start, iotaHelper(start + 1, stop))

def iota(n : Int) : List[Int] = iotaHelper(0, n)

def randomInts(n : Int) : List[Int] =
  if (n == 0) Nil()
  else Cons(util.Random.nextInt(), randomInts(n - 1))

def insertAll[T](empty : T, insert : (Int, T) => T, values : List[Int]) : T =
  values match {
    case Nil() => empty
    case Cons(n, rest) => insert(n, insertAll(empty, insert, rest))
  }

def isMemberAll[T](isMember : (Int, T) => Boolean, values : List[Int], set : T) : Boolean =
  values match {
    case Nil() => true
    case Cons(n, rest) => isMember(n, set) && isMemberAll(isMember, rest, set)
  }

def generateRandomData(sizes : List[Int]) : List[(Int, List[Int])] = sizes match {
  case Nil() => Nil()
  case Cons(n, rest) => Cons((n, randomInts(n)), generateRandomData(rest))
}

def insertAllOrderedList(values : List[Int]) : List[Int] =
  insertAll(List.empty[Int], (n : Int, lst : List[Int]) => List.insert(compareInt, n, lst), values)

def benchmarkInsertAll(lst : List[(Int, List[Int])]) : List[(Int, Double)] =
  lst match {
    case Nil() => Nil()
    case Cons((len, data), rest) => {
      val avgTime = averageTime(10, insertAllOrderedList, data)
      Cons((len, avgTime), benchmarkInsertAll(rest))
    }
  }


println(benchmarkInsertAll(generateRandomData(Cons(500, Cons(1000, Cons(2000, Cons(4000, Nil())))))))
