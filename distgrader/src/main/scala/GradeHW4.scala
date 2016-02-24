package grading

object GradeHW4 {

  import scala.concurrent._
  import scala.concurrent.duration._
  import edu.umass.cs.zip._
  import Scripting._

  def main(): Unit = {

    val scripting = new grading.Scripting("10.8.0.6")
    import scripting.system.dispatcher

    val prefix =
      """
      import FunctionalDataStructures._

      val emptyQueue = Queue[Int](Nil, Nil)

      def gt(x: Int, y: Int) = x > y
      def add1(x: Int) = x + 1
      def isEven(x: Int) = x % 2 == 0

      def fromList[A](lst: List[A]): JoinList[A] = lst match {
        case Nil => Empty()
        case List(x) => Singleton(x)
        case _  => {
          val len = lst.length
          val (lhs, rhs) = lst.splitAt(len / 2)
          Join(fromList(lhs), fromList(rhs), len)
        }
      }

      def toList[A](lst: JoinList[A]): List[A] = lst match {
        case Empty() => Nil
        case Singleton(x) => List(x)
        case Join(lst1, lst2, _) => toList(lst1) ++ toList(lst2)
      }

      val jl1 = fromList(List(10, 20, 30, 40, 50, 60, 70))
      """

    def testBuilder(zip: ZipBuilder, body: String): Unit = {
      zip.add(
        s"object GradingMain extends App { $prefix; $body }".getBytes,
        "src/main/scala/GradingMain.scala")
    }

    val lst = Scripting.assignments("hw4").map(dir => {
      updateState(dir.resolve("grading.json")) { case report =>

        SBTTesting.testWithSbt(scripting, dir, testBuilder, report) {  case root =>
          val compiles = root.thenCompile("Is the object FunctionalDataStructures defined?", "()")

          val enqueue = compiles.thenCompile("Does enqueue have the right type?",
            """def foo[A](elt: A, q: Queue[A]): Queue[A] = enqueue(elt, q)""", score = 0)

          val dequeue = compiles.thenCompile("Does dequeue have the right type?",
            """def foo[A](q: Queue[A]): Option[(A, Queue[A])] = dequeue(q)""", score = 0)


          enqueue.thenRun("Does enqueue add to the back?",
            """assert(enqueue(1, enqueue(2, enqueue(3, emptyQueue))) == Queue(Nil, List(1, 2, 3)))""")

          dequeue.thenRun("Does dequeue remove from the front if it has elements?",
          """assert(dequeue(Queue(List(1, 2, 3), List(6, 5, 4))) match {
               case Some((1, _)) => true
               case _ => false
            })""")

          dequeue.thenRun("Does dequeue remove from the back if the front is empty?",
            """assert(dequeue(Queue(Nil, List(3, 2, 1))) match {
                 case Some((1, _)) => true
                case _ => false
              })""")


          dequeue.thenRun("Does dequeue reverse the back as specified?",
            """assert(dequeue(Queue(Nil, List(3, 2, 1))) match {
                 case Some((1, Queue(List(2, 3), Nil))) => true
                 case _ => false
               })""")

          dequeue.thenRun("Does dequeue work on empty queues?",
            """assert(dequeue(emptyQueue) == None)""")


          val max = compiles.thenCompile("Does max have the right type?",
            """def foo[A](lst: JoinList[A], compare: (A, A) => Boolean): Option[A] = max(lst, compare)""", score = 0)

          val first = compiles.thenCompile("Does first have the right type?",
            """def f[A](lst: JoinList[A]): Option[A] = first(lst)""", score = 0)

          val rest = compiles.thenCompile("Does rest have the right type?",
          """def f[A](lst: JoinList[A]): Option[JoinList[A]] = rest(lst)""", score = 0)

          val nth = compiles.thenCompile("Does nth have the right type?",
             """def f[A](lst: JoinList[A], n: Int): Option[A] = nth(lst, n)""", score = 0)

          val map = compiles.thenCompile("Does map have the right type?",
             """def f[A,B](f: A => B, lst: JoinList[A]): JoinList[B] = map(f, lst)""", score = 0)

          val filter = compiles.thenCompile("Does filter have the right type?",
          """def f[A](pred: A => Boolean, lst: JoinList[A]): JoinList[A] = filter(pred, lst)""", score = 0)


          max.thenRun("Does max(Empty()) produce None?",
            """assert(max(Empty[Int](), gt) == None)""")

          max.thenRun("Does max on a singleton produce the value?",
            "assert(max(Singleton(100), gt) == Some(100))")

          max.thenRun("Does max on a JoinList that only has 1 Singleton produce the value?",
            "assert(max(Join(Singleton(100), Empty(), 1), gt) == Some(100))")

          max.thenRun("Does max produce the maximum on a larger JoinList?",
            "assert(max(fromList(List(1, 7, 2, 3, 9, 200, 1)), gt) == Some(200))")

          max.thenRun("Does max work with a non-integer compare function?",
            """def f(x: String, y: String): Boolean = x == "MAX"
               assert(max(fromList(List("a", "b", "MAX", "c", "d", "e")), f) == Some("MAX"))""")


          map.thenRun("Does map work on empty lists?",
            "assert(map(add1, Empty()) == Empty())")


          map.thenRun("Does map produce singletons on singleton lists?",
            "assert(map(add1, Singleton(10)) == Singleton(11))")

          map.thenRun("Does map work on larger lists?",
            "assert(toList(map(add1, fromList(List(1, 2, 3, 4, 5, 6)))) == List(2, 3, 4, 5, 6, 7))")


          filter.thenRun("Does filter work on empty lists?",
            "assert(filter(isEven, Empty[Int]()) == Empty())")

          filter.thenRun("Does filter produce empty when filtering out a singleton?",
            "assert(filter(isEven, Singleton(1)) == Empty())")

          filter.thenRun("Does filter work on larger lists?",
            "assert(toList(filter(isEven, fromList(List(1,2, 3, 4, 5, 6, 7, 8)))) == List(2, 4, 6, 8))")

          first.thenRun("Does first(Empty) produce None?",
            "assert(first(Empty[Int]()) == None)")

          first.thenRun("Does first(Singleton(x)) produce Some(x)?",
            "assert(first(Singleton(10)) == Some(10))")

          first.thenRun("Does first work on larger lists?",
            "assert(first(fromList(List(10, 20, 30, 40, 50, 60, 70))) == Some(10))")

          rest.thenRun("Does rest(Empty) produce None?",
            "assert(rest(Empty[Int]()) == None)")

          rest.thenRun("Does rest(Singleton(x)) produce Some(Empty())?",
            "assert(rest(Singleton(10)) == Some(Empty[Int]()))")

          rest.thenRun("Does rest work on larger lists?",
            """rest(jl1) match {
              case Some(tail) => assert(toList(tail) == List(20, 30, 40, 50, 60, 70))
              case None  => assert(false)
            }""")

          nth.thenRun("Does nth(Empty, _) produce None?",
            "assert(nth(Empty[Int], 0) == None)")

          nth.thenRun("Does nth(Singleton(x), 0) produce Some(x)?",
            "assert(nth(Singleton(42), 0) == Some(42))")

          nth.thenRun("Does nth(Singleton(x), 100) produce None?",
            "assert(nth(Singleton(1), 100) == None)")


          nth.thenRun("Does nth work with a valid index on a larger lists?",
            "assert(nth(jl1, 1) == Some(20))")


          nth.thenRun("Does nth produce None when indexing out-of-bounds on a larger list?",
            "assert(nth(jl1, 50) == None)")

        }
      }
    })

    val result = Await.result(Future.sequence(lst), Duration.Inf)
    println("Grading jobs complete")

    scripting.system.terminate()

  }

}
