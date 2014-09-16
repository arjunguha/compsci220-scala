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

test("timing insertAllOrdList on ordered input") {
  // You may need to tweak this data to suit your computer
  val data = List(revOrder(500), revOrder(1000), revOrder(2000), revOrder(4000))
  val trials = 10
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllOrdList, values)), data)
  val (slope, intercept, rSq) = linearRegression(results)
  assert (math.abs(1.0 - rSq) <= 0.1)
}

test("timing insertAllOrdList on random input") {
  // You may need to tweak this data to suit your computer
  val data = List(randomInts(500), randomInts(1000), randomInts(2000), randomInts(4000))
  val trials = 5
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllOrdList, values)), data)
  val (slope, intercept, rSq) = logRegression(results)
  println(rSq)
  assert (math.abs(1.0 - rSq) <= 0.6)
}

test("timing insertAllOrdList on random input") {
  // You may need to tweak this data to suit your computer
  val data = List(randomInts(500), randomInts(1000), randomInts(2000), randomInts(4000))
  val trials = 5
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllOrdList, values)), data)
  val (slope, intercept, rSq) = logRegression(results)
  println(rSq)
  assert (math.abs(1.0 - rSq) <= 0.6)
}

test("timing insertAllAVL on random input") {
  // You may need to tweak this data to suit your computer
  val data = List(randomInts(500), randomInts(1000), randomInts(2000), randomInts(4000))
  val trials = 5
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllAVL, values)), data)
  val (slope, intercept, rSq) = logRegression(results)
  println(rSq)
  assert (math.abs(1.0 - rSq) <= 0.6)
}

test("timing insertAllAVL on random input") {
  // You may need to tweak this data to suit your computer
  val data = List(randomInts(500), randomInts(1000), randomInts(2000), randomInts(4000))
  val trials = 5
  // A list of pairs: List((size1, time1), (size2, time2), ...)
  val results = map((values: List[Int]) => (length(values), averageTime(trials, insertAllAVL, values)), data)
  val (slope, intercept, rSq) = logRegression(results)
  println(rSq)
  assert (math.abs(1.0 - rSq) <= 0.6)
}