package cmpsci220.grading

import cmpsci220.hw.sudoku._
import grader.TestSuite

class Sudoku(targetYaml: String, solution: SudokuLike) {

  import solution._

  TestSuite(targetYaml) { builder =>

    import builder._

    test("Does your solution build -- not final grade") {
      assert(true)
    }
  }

}
