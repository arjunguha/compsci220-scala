object NQueens {

  var nextVar = 0
  def freshVar() = {
    nextVar = nextVar + 1
    Var(s"x$nextVar")
  }

  def solve(dim: Int) = {
    val coords = 0.until(dim).map(x => 0.until(dim).map(y => (x, y))).flatten
    val grid = coords.map(p => p -> (Var(s"p${p._1},${p._2}") : Expr)).toMap

    val rows = coords.groupBy(p => p._1).values.toSeq.map(_.map(x => grid(x)))
    val cols = coords.groupBy(p => p._2).values.toSeq.map(_.map(x => grid(x)))
    val diags = coords.groupBy(p => p._1 + p._2).values.toSeq.map(_.map(x => grid(x)))
    val antiDiags = coords.groupBy(p => p._2 - p._2).values.toSeq.map(_.map(x => grid(x)))

    def atMostOne(lst: Seq[Expr]): Expr = {
      And(lst.tails.toSeq.dropRight(2).map { case p +: others => {
        p implies And(others.map(x => !x): _*)
      } }: _*)
    }

    def onePer(lst: Seq[Expr]): Expr = {
      println("onePer: " + lst)
      And(lst.tails.toSeq.dropRight(1).map { case p +: others => {
        p implies And(others.map(x => !x): _*)
      } }: _*)
    }

    println(rows.toList)
    val onePerRowCol = And((rows ++ cols).map(onePer): _*)
    val atmostOnePerDiag = And((diags ++ antiDiags).map(atMostOne): _*)

    val formula = onePerRowCol && atmostOnePerDiag

    Solution.fastSolve(formula)
  }

}