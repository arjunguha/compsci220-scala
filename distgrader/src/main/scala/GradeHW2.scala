package grading

class HW2Grading(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add("""addSbtPlugin("edu.umass.cs" % "compsci220" % "1.0.0")""".getBytes, "project/plugins.sbt")
    zip.add(s"""
      object GradingMain extends App {
        case class A(x: Int)
        case class B(y: Int)
        case class C(z: Int)

        def combine(x: A, y: B): C = C(x.x + y.y)
        def aLT(x: A, y: A): Boolean = x.x < y.x

        import HOF._
        $body
      }
      """.getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Check that object HOF is defined", "()")

    val map2OK = compiles.thenCompile(
      "Does map2 have the right type?",
      """def foo[A,B,C](f: (A, B) => C, lst1: List[A], lst2: List[B]): List[C] = map2(f, lst1, lst2)""")

    map2OK.thenRun(
      "Does map2 work on empty lists?",
      """assert(map2(combine, Nil, Nil) == Nil)""")

    map2OK.thenRun(
      "Does map2 work on non-empty lists?",
      """assert(map2(combine, List(A(10), A(20), A(30)), List(B(1), B(2), B(3))) == List(C(11), C(22), C(33)))""")

    val zipOK = compiles.thenCompile(
      "Does zip have the right type?",
      """def foo[A,B](lst1: List[A], lst2: List[B]): List[(A, B)] = zip(lst1, lst2)""")

    zipOK.thenRun(
      "Does zip work on empty lists?",
      """assert(zip[A, B](Nil, Nil) == Nil)""")

    zipOK.thenRun(
      "Does zip work on non-empty lists?",
      """assert(zip(List(A(10), A(20)), List(B(50), B(90))) == List((A(10), B(50)), (A(20), B(90))))""")

    val flattenOK = compiles.thenCompile(
      "Does flatten have the right type?",
      """def foo[A](lst: List[List[A]]): List[A] = flatten(lst)""")

    flattenOK.thenRun(
      "Does flatten work on empty lists?",
      """assert(flatten(Nil) == Nil)""")

    flattenOK.thenRun(
      "Does flatten work when there are empty lists in the input?",
      """assert(flatten(List(List(1), Nil, List(2))) == List(1, 2))""")

    flattenOK.thenRun(
      "Does flatten work when all sub-lists are non-empty?",
      """assert(flatten(List(List(1,2), List(4, 5), List(10, 11))) == List(1,2,4,5,10,11))""")

    val flatten3OK = compiles.thenCompile(
      "Does flatten3 have the right type?",
      """def foo[A](lst: List[List[List[A]]]): List[A] = flatten3(lst)""")

    flatten3OK.thenRun(
      "Does flatten3 work on an empty list?",
      """assert(flatten3(Nil) == Nil)""")

    flatten3OK.thenRun(
      "Does flatten3 work on a non-empty list?",
      """assert(flatten3(List(List(List(1,2,3), List(4,5,6)), List(List(7, 8, 9)))) == List(1,2,3,4,5,6,7,8,9))""")

    val buildListOK = compiles.thenCompile(
      "Does buildList have the right type?",
      """def foo[A](n: Int, f: Int => A): List[A] = buildList(n, f)""")

    buildListOK.thenRun(
      "Does buildList work when n = 0?",
      """assert(buildList(0, (n: Int) => ???) == Nil)""")

    buildListOK.thenRun(
      "Does buildLIst work when n > 0?",
      """assert(buildList(3, (n: Int) => A(n)) == List(A(0), A(1), A(2)))""")

    val mapListOK = compiles.thenCompile(
      "Does mapList have the right type?",
      """def foo[A,B](f: A => List[B], lst: List[A]): List[B] = mapList(lst, f)""")

    mapListOK.thenRun(
      "Does mapList work on empty lists?",
      """assert(mapList(Nil, (n: Int) => ???) == Nil)""")

    mapListOK.thenRun(
      "Does mapList work on non-empty lists?",
      """assert(mapList[Int,Int](List(1,2,3), ((n: Int) => List(n, -n))) == List(1, -1, 2, -2, 3, -3))""")

    val partitionOK = compiles.thenCompile(
      "Does partition have the right type?",
      """def foo[A](f: A => Boolean, lst: List[A]): (List[A], List[A]) = partition(f, lst)""")

    partitionOK.thenRun(
      "Does partition work on empty lists?",
      """assert(partition((x: Int) => true, Nil) == (Nil, Nil))""")

    partitionOK.thenRun(
      "Does partition work on non-empty lists?",
      """assert(partition((x: Int) => x % 2 == 0, List(0, 1, 2, 3)) == (List(0, 2), List(1, 3)))""")

    val mergeOK = compiles.thenCompile(
      "Does merge have the right type?",
      """def foo[A](lt: (A, A) => Boolean, alist1: List[A], alist2: List[A]): List[A] = merge(lt, alist1, alist2)""")

    mergeOK.thenRun(
      "Does merge work when lhs is Nil?",
      """val actual = merge(aLT, Nil, List(A(1), A(2), A(3)))
         val expected = List(A(1), A(2), A(3))
         assert(actual == expected || actual == expected.reverse)""")
    
    mergeOK.thenRun(
      "Does merge work when rhs is Nil?",
      """val actual = merge(aLT, List(A(1), A(2), A(3)), Nil)
         val expected = List(A(1), A(2), A(3))
         assert(actual == expected || actual == expected.reverse)""")
    
    mergeOK.thenRun(
      "Does merge interleave non-empty lists correctly?",
      """
         val expected = List(A(1), A(2), A(3), A(4), A(5), A(6))
         val r = try {
           val actual = merge(aLT, List(A(1), A(3), A(5)), List(A(2), A(4), A(6)))
           actual == expected || actual == expected.reverse
         }
         catch {
           case exn: Throwable => false
         }
         if (r == false) {
           val actual = merge(aLT, List(A(5), A(3), A(1)), List(A(6), A(4), A(2)))
           assert(actual == expected || actual == expected.reverse)
         }""")
    
    val sortOK = compiles.thenCompile(
      "Does sort have the right type?",
      """def foo[A](lt: (A, A) => Boolean, alist: List[A]): List[A] = sort(lt, alist)""")
    
    sortOK.thenRun(
      "Does sort work on Nil?",
      """assert(sort(aLT, Nil) == Nil)""")
    
    sortOK.thenRun(
      "Does sort work on an already sorted list?",
      """val actual = sort(aLT, List(A(1), A(2), A(3), A(4), A(5)))
         val expected = List(A(1), A(2), A(3), A(4), A(5))
         assert(actual == expected || actual == expected.reverse)""")
    
    sortOK.thenRun(
      "Does sort work on a reverse-sorted list?",
      """val actual = sort(aLT, List(A(5), A(4), A(3), A(2), A(1)))
         val expected = List(A(5), A(4), A(3), A(2), A(1))
         assert(actual == expected || actual == expected.reverse)""")
    
    sortOK.thenRun(
      "Does sort work on an unsorted list?",
      """val actual = sort(aLT, List(A(1), A(3), A(2), A(5), A(4)))
         val expected = List(A(5), A(4), A(3), A(2), A(1))
         assert(actual == expected || actual == expected.reverse)""")
    
  }
}
