package cmpsci220.grading

import cmpsci220.hw.sudoku._
import grader.TestSuite

class Sudoku(targetYaml: String, solution: SudokuLike) {

  import solution._

  val allPositions = for (r <- 0.to(8); c <- 0.to(8)) yield { (r, c) }

  // Produces true if the board does not exclude the given solution
  private def canFindSolution(solutionStr: String) = {
    val sol = solutionStr.toArray
    (board: solution.T) => {
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
  }

  TestSuite(targetYaml) { builder =>

    import builder._

    test("Do parse and valueAt work?") {
      val str = ".1.....2..3..9..1656..7...33.7..8..........89....6......6.254..9.5..1..7..3.....2"
      val b = parse(str)
      for (r <- 0.to(8); c <- 0.to(8)) {
        str(r * 9 + c) match {
          case '.' => ()
          case ch => assert(b.valueAt(r, c) == Some(ch.toString.toInt))
        }
      }
    }

    test("Does availableValuesAt work (basic test)?") {
      val str = "1................................................................................"
      val b = parse(str)
      assert(b.availableValuesAt(0, 1).toSet == Set(2,3,4,5,6,7,8,9))
    }

    test("Does availableValuesAt work (row elimination)?") {
      val str = "12..............................................................................."
      val b = parse(str)
      assert(b.availableValuesAt(0, 2).toSet == Set(3,4,5,6,7,8,9))
    }

    test("Does availableValuesAt work (column elimination)?") {
      val str = "1.......................................................................2........"
      val b = parse(str)
      assert(b.availableValuesAt(2, 0).toSet == Set(3,4,5,6,7,8,9))
    }

    test("Does availableValuesAt work (box elimination)?") {
      val str = "1................................................................................"
      val b = parse(str)
      assert(b.availableValuesAt(1, 1).toSet == Set(2,3,4,5,6,7,8,9))
    }

    test("Does isSolved work?") {
      assert(parse("853697421914238675762145893128563947475982136396471582581724369637859214249316758").isSolved)
    }

    // No tests for isUnsolvable. There are too many ways to implement this check.

    test("Does place work?") {
      val str = "12..............................................................................."
      assert(parse(str).place(0, 2, 3).valueAt(0, 2) == Some(3))
    }

    test("Does solve work (search space has less than 128 states)?") {
      val str = ".................5...145893128563947475982136396471582581724369637859214249316758"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    }

    test("Does solve work (search space has less than 300 states)?") {
      val str = ".................5...145..3128563947475982136396471582581724369637859214249316758"
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    }

    test("Does solve work (search space has less than 1000 states)?") {
      val str = ".................5...145...1285639474759821363964715.2581724369637859.14........."
      val b = parse(str).solve.get
      assert(b.isSolved)
      assert(canFindSolution(str)(b))
    }
  }

}
