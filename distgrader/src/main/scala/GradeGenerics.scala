package grading

class GradeGenerics(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix = """
    import hw.generics._

  """

  val binTreeToList = """
    def binTreeToList[A](t: BinTree[A]): List[A] = t match {
      case Leaf() => List()
      case Node(t1, x, t2) => binTreeToList(t1) ++ (x :: binTreeToList(t2))
    }
  """

  val myListLike = """
    sealed trait L[A] extends ListLike[A, L[A]]

    case class C[A](hd: A, tl: L[A]) extends L[A] {
      def isEmpty(): Boolean = false
      def head() = Some(hd)
      def tail() = Some(tl)
      def cons(newHd: A) = new C(newHd, this)
    }

    case class E[A]() extends L[A] {
      def isEmpty(): Boolean = true
      def head() = None
      def tail() = None
      def cons(newHd: A) = new C(newHd, this)
    }
  """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add("""addSbtPlugin("edu.umass.cs" % "compsci220" % "1.0.2")""".getBytes, "project/plugins.sbt")  
    zip.add(s"object GradingMain extends App { $prefix\n$body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val binTree = root.thenCompile("Is the BinTree type properly defined?",
      """def f[A](t: BinTree[A]): List[A] = t match {
           case Leaf() => List()
           case Node(t1, x, t2) => f(t1) ++ List(x) ++ f(t2)
         }""")

    val listLikeTree = binTree.thenCompile("Does BinTree extend ListLike?",
      """
      def f[E, L](l: ListLike[E, L]): Int = 0

      def g[A](t: BinTree[A]): Int = f[A, BinTree[A]](t)
      """)

    listLikeTree.thenRun("Does head return the leftmost child of a BinTree?",
      """
        val t = Node(Node(Leaf(), 20, Leaf()), 30, Node(Leaf(), 40, Leaf()))
        assert(t.head() == Some(20))
      """)

    listLikeTree.thenRun("Does head produce None when applied to a Leaf?",
      """
        assert(Leaf[Int]().head() == None)
      """)

    listLikeTree.thenRun("Does tail produce None when applied to a Leaf?",
      """
        assert(Leaf[Int]().tail() == None)
      """)

    listLikeTree.thenRun("Does tail work on non-leaves?",
     binTreeToList ++
     """
     val t = Node(Node(Leaf(), 20, Leaf()), 30, Node(Leaf(), 40, Leaf()))
     assert(binTreeToList(t.tail.get) == List(30, 40))""")

    listLikeTree.thenRun("Does cons work?",
     binTreeToList ++
     """
     val t = Node(Node(Leaf(), 20, Leaf()), 30, Node(Leaf(), 40, Leaf()))
     assert(binTreeToList(t.cons(100)) == List(100, 20, 30, 40))""")

    listLikeTree.thenRun("Does isEmpty produce true on leaves?",
       """assert(Leaf[Int]().isEmpty == true)""")

    listLikeTree.thenRun("Does isEmpty produce false on nodes?",
       """assert(Node(Node(Leaf(), 20, Leaf()), 30, Node(Leaf(), 40, Leaf())).isEmpty == false)""")

    val filterDefined = root.thenCompile(
      "Is filter defined with the right type?",
      """
      def f[E, C <: ListLike[E, C]](f: E => Boolean, alist: C): C = {
        ListFunctions.filter(f, alist)
      }
      """)

    filterDefined.thenRun("Does filter work?",
      myListLike ++
      """
        assert(ListFunctions.filter[Int, L[Int]]((x: Int) => x == 0, C(1, C(0, C(0, E()))))
               == C(0, C(0, E())))
      """)

    val appendDefined = root.thenCompile(
      "Is append defined with the right type?",
      """
      def f[E, C <: ListLike[E, C]](alist1: C, alist2: C): C = {
        ListFunctions.append[E, C](alist1, alist2)
      }
      """)

    appendDefined.thenRun("Does append work?",
      myListLike ++
      """
        assert(ListFunctions.append[Int, L[Int]](C(1, C(2, E())), C(3, C(4, E()))) ==
               C(1, C(2, C(3, C(4, E())))))
      """)

    val sortDefined = root.thenCompile(
      "Is sort defined with the right type?",
      """
        def f[A <: Ordered[A], C <: ListLike[A, C]](alist: C): C = {
          ListFunctions.sort[A, C](alist)
        }
      """)

    sortDefined.thenRun("Does sort work?",
      myListLike ++
      """
      case class XInt(n: Int) extends Ordered[XInt] {

        def compare(other: XInt) = {
          if (this.n == other.n) EQ
          else if (this.n < other.n) LT
          else GT
        }
      }
      val sorted = ListFunctions.sort[XInt, L[XInt]](C(XInt(3), C(XInt(1), C(XInt(2), E()))))
      assert(sorted == C(XInt(1), C(XInt(2), C(XInt(3), E()))) ||
             sorted == C(XInt(3), C(XInt(2), C(XInt(1), E()))))
      """)

    root.thenCompile("Does C1 extend T2?",
      """
      def f(t2: T2[Int, Int, String, String]) = 0
      f(new C1())
      """)

    root.thenCompile("Does C2 extend T1?",
      """
      def f(x: T1[Int, Int]) = 0
      f(new C2())
      """)

    root.thenCompile("Does C3 extend T3?",
      """
      def f[A](x: T3[Int, A, Int, A, String, String, A]) = {
        0
      }
      def g[A](x: C3[A]) = f(x)
      """)

    root.thenCompile("Does C4 extend T3?",
      """
      def f[A](x: T3[Int, C4[A], C4[A], Int, C4[A], C4[A], Int]) = {
         0
      }
      def g[A](x: C4[A]) = f(x)
      """)
    }
}

