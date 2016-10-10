package grading

class GradingTicTacToe(val assignmentRoot: String, val selfIP: String) extends TestFramework {

  val prefix =
    """
    import Solution._

    // for tic tac toe, these are whitespace characters that are ignored. They
    // let us write the board in 2D
    val whitespaces = "|\n -".toList

    // A . indicates a blank square
    val validChars = "XO.".toList

    def parse(str: String): Map[(Int, Int), Player] = {
      val lst = str.toList.filter(ch => !whitespaces.contains(ch))
      require(lst.length == 9)
      require(lst.forall(ch => validChars.contains(ch)))
      val coords = for (y <- 0.to(2); x <-0.to(2)) yield { (x, y) }
      coords.zip(lst).foldLeft(Map[(Int, Int), Player]()) {
        case (matrix, ((x, y), 'X')) => matrix + ((x, y) -> X)
        case (matrix, ((x, y), 'O')) => matrix + ((x, y) -> O)
        case (matrix, ((x, y), '.')) => matrix
        case _ => sys.error("should never happen")
      }
    }

    private def myCreateGame(str: String, turn: Player) = {
      val b = parse(str)
      createGame(turn, 3, b)
    }

    """

  def zipBuilder(zip: edu.umass.cs.zip.ZipBuilder, body: String): Unit = {
    zip.add("""addSbtPlugin("edu.umass.cs" % "compsci220" % "1.0.2")""".getBytes, "project/plugins.sbt")  
    zip.add(s"object GradingMain extends App { $prefix $body }".getBytes,
      "src/main/scala/GradingMain.scala")
  }

  def body(root: TestCase): Unit = {
    val compiles = root.thenCompile("Is the Solution object defined?", "()")

    compiles.thenRun("Does isFinished produce true on a full board?",
      """val b = myCreateGame("XOXOXOOXO", X)
      assert(b.isFinished)""")

    compiles.thenRun("Does isFinished produce false on an empty board?",
      """val b = myCreateGame(".........", X)
         assert(!b.isFinished)""")

    compiles.thenRun("Does getWinner find a winner in a row?",
      """val b = myCreateGame("O.OXXX.O.", X)
         assert(b.getWinner == Some(X))""")

    compiles.thenRun("Does getWinner find a winner in a column?",
      """val b = myCreateGame("OOXXOX.O.", X)
         assert(b.getWinner == Some(O))""")

    compiles.thenRun("Does getWinner find a winner on the anti-diagonal?",
      """val b = myCreateGame("OOXOXXX..", X)
        assert(b.getWinner == Some(X))""")

    compiles.thenRun("Does getWinner find a winner on the  diagonal?",
      """val b = myCreateGame("XOOXXO..X", X)
      assert(b.getWinner == Some(X))""")

    compiles.thenRun("Does nextBoards produce nine next-boards for the empty board?",
      """val b = myCreateGame(".........", X)
      assert(b.nextBoards.length == 9)""")

    compiles.thenRun("Does nextBoards produce 3 next-boards when there are three spots left?",
      """val b = myCreateGame("XOXXOX...", O)
         assert(b.nextBoards.length == 3)""")

    compiles.thenRun("Does minimax report draw on the empty board?",
      """val b = myCreateGame(".........", X)
      assert(minimax(b) == None)""")

    compiles.thenRun("Does minimax report X as winner on this board (in one move)?",
      """val b = myCreateGame("XOXOXO.XO", X)""")

    compiles.thenRun("Does minimax report X as winner on this board (in two moves by X)?",
      """val b = myCreateGame("X.X.O.X.O", O)""")
  }
}

