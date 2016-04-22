package grading

class GradeSudoku(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix =
    """
      import hw.sudoku._
      import Solution._

      val allPositions = for (r <- 0.to(8); c <- 0.to(8)) yield { (r, c) }

      // Produces true if the board does not exclude the given solution
      private def canFindSolution(solutionStr: String) = {
        val sol = solutionStr.toArray
        val forward = (board: Solution.T) => {
          allPositions.forall { case (r, c) =>
            sol(r * 9 + c) match {
              case '.' => true
              case ch =>  board.valueAt(r, c) match {
                case Some(v) => v == ch.toString.toInt
                case None => board.availableValuesAt(r, c).toSet.contains(ch.toString.toInt)
              }
            }
          }
        }
        val backward = (board: Solution.T) => {
          allPositions.forall { case (r, c) =>
            sol(r * 9 + c) match {
              case '.' => true
              case ch =>  board.valueAt(c, r) match {
                case Some(v) => v == ch.toString.toInt
                case None => board.availableValuesAt(c, r).toSet.contains(ch.toString.toInt)
              }
            }
          }
        }
        (board: Solution.T) => forward(board) || backward(board)
      }
      """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
      zip.add("""
        resolvers += "PLASMA" at "https://dl.bintray.com/plasma-umass/maven"
        libraryDependencies += "edu.umass.cs" %% "compsci220" % "1.0.1"
        """.getBytes,
        "build.sbt")
    zip.add(s"object GradingMain extends App { $prefix $body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Does the program compile?", "()")

    compiles.thenRun("Do parse and valueAt work?", """
      val str = ".1.....2..3..9..1656..7...33.7..8..........89....6......6.254..9.5..1..7..3.....2"
      val b = parse(str)
      for (r <- 0.to(8); c <- 0.to(8)) {
        str(r * 9 + c) match {
          case '.' => ()
          case ch => assert(b.valueAt(r, c) == Some(ch.toString.toInt))
        }
      }
    """)

    compiles.thenRun("Does availableValuesAt work (basic test)?", """
      val str = "1................................................................................"
      val b = parse(str)
      assert(b.availableValuesAt(0, 1).toSet == Set(2,3,4,5,6,7,8,9))
    """)

    compiles.thenRun("Does availableValuesAt work (row elimination)?", """
      val str = "12..............................................................................."
      val b = parse(str)
      assert(b.availableValuesAt(0, 2).toSet == Set(3,4,5,6,7,8,9))
    """)

    compiles.thenRun("Does availableValuesAt work (column elimination)?", """
      val str = "1.......................................................................2........"
      val b = parse(str)
      assert(b.availableValuesAt(2, 0).toSet == Set(3,4,5,6,7,8,9))
    """)

    compiles.thenRun("Does availableValuesAt work (box elimination)?", """
      val str = "1................................................................................"
      val b = parse(str)
      assert(b.availableValuesAt(1, 1).toSet == Set(2,3,4,5,6,7,8,9))
    """)

    compiles.thenRun("Does isSolved work?", """
      assert(parse("853697421914238675762145893128563947475982136396471582581724369637859214249316758").isSolved)
    """)

    // No tests for isUnsolvable. There are too many ways to implement this check.

    compiles.thenRun("Does place work?", """
      val str = "12..............................................................................."
      assert(parse(str).place(0, 2, 3).valueAt(0, 2) == Some(3))
    """)

    compiles.thenRun("Does solve work (search space has less than 128 states)?", """
      val str = ".................5...145893128563947475982136396471582581724369637859214249316758"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

    compiles.thenRun("Does solve work (search space has less than 300 states)?", """
      val str = ".................5...145..3128563947475982136396471582581724369637859214249316758"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

    compiles.thenRun("Does solve work (search space has less than 1000 states)?", """
      val str = ".................5...145...1285639474759821363964715.2581724369637859.14........."
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

    compiles.thenRun("CS121_1 puzzle", """
      val str = "85....4.1......67...21....3..85....7...982...3....15..5....43...37......2.9....58"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

    compiles.thenRun("CS121_2 puzzle", """
      val str = ".1.....2..3..9..1656..7...33.7..8..........89....6......6.254..9.5..1..7..3.....2"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

    compiles.thenRun("puz1 puzzle", """
      val str = ".43.8.25.6.............1.949....4.7....6.8....1.2....382.5.............5.34.9.71."
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

    compiles.thenRun("puz2 puzzle", """
      val str = "2...8.3...6..7..84.3.5..2.9...1.54.8.........4.27.6...3.1..7.4.72..4..6...4.1...3"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    """)

// val fromCS121_1 =
// val fromCS121_2 =
// val puz1 =
// val puz2 =
  }
}
