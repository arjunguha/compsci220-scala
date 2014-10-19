import org.scalatest._
import Solution._
import java.nio.file._

class TestSuite extends FunSuite {

  test("all cells have 20 peers") {
    for (row <- 0.to(8)) {
      for (col <- 0.to(8)) {
        // 8 row-peers + 8 col-peers + 4 box peers
        assert(peers(row, col).toSet.size == 20, s"peers($row, $col)")
      }
    }
  }

  //test("easy puzzles") {

    val puzzles = new String(Files.readAllBytes(Paths.get("easy.txt")))
                  .split('\n')

    for (puzzle <- puzzles) {
        test(puzzle) {

      val board = parse(puzzle)
      println(s"Parsed $puzzle")
      val solution = board.solve

      assert(solution != None)
      println("Solved")
      }
    }
 // }

}