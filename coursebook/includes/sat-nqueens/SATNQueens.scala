/*
  Save this file as SAT-NQueens.scala and run

  scala SAT-NQueens N | z3 -in -smt2

  On my laptop, this solves for N = 100 in a few seconds.

  Simply running the command will print a model which is quite unreadable. I'll leave it to you to write a
  verifier for the solution. But, this quick hack verifies that it only places N queens:

  scala SAT-NQueens N | z3 -in -smt2 | wc -l  # should output N

*/
val n = args(0).toInt

def exactlyOne(vars: List[String]): String = {
  val str = vars.map(xTrue => {
    val rest = vars.filter(_ != xTrue).map(y => s"(not $y)").mkString(" ")
    s"(and $xTrue $rest)"
  }).mkString(" ")
  "(or " + str + ")"
}

def none(vars: List[String]): String = {
  "(and " + vars.map(x => s"(not $x)").mkString(" ") + ")"
}

def atMostOne(vars: List[String]): String = {
  "(or " + exactlyOne(vars) + " " + none(vars) + ")"
}

val board: Map[(Int, Int), String] =
  0.until(n).map(x => 0.until(n).map(y => (x, y) -> s"x-$x-$y")).flatten.toMap

val positions = board.keySet.toList

for (name <- board.values) {
  println(s"(declare-const $name Bool)")
}

for (y <- 0.until(n)) {
  println("(assert " + exactlyOne(0.until(n).map(x => board((x, y))).toList) + ")")
}

for (x <- 0.until(n)) {
  println("(assert " + exactlyOne(0.until(n).map(y => board((x, y))).toList) + ")")
}

val diags = (positions.groupBy(p => p._1 + p._2).values ++ positions.groupBy(p => p._1 - p._2).values).toList

for (diag <- diags) {
  println("(assert " + atMostOne(diag.map(x => board(x))) + ")")
}

println("(check-sat)")
println("(get-model)")