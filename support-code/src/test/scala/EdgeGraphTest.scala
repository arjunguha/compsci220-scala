import edgemaze._
import scala.collection.immutable.Queue

class EdgeGraphSuite extends FunSuite {
    val start = 0
    test("Medium test graph: Reachable") {
        assert(reachable(TestGraphs.medium, start) ==
            Set(0,1,2,3,4,5,6,7,8,9,10))
    }

    test("Medium test graph: DFSPath") {
        assert(dfsPath(TestGraphs.medium, start) ==
            Array(-1, 0, 1, 2, 2, 4, 4, 6, 7, 8, 9))
    }

    test("Medium test graph: DFSPath: Path to node 10") {
        assert(makePath(dfsPath(TestGraphs.medium, start), 10) ==
            List(0, 1, 2, 4, 6, 7, 8, 9, 10))
    }

    test("Medium test graph: DFSPath: Path to node 10 Length") {
        val p = makePath(dfsPath(TestGraphs.medium, start), 10)
        assert(pathLength(p) == 11)
    }

    test("Medium test graph: Dijkstra: Path to node 10") {
        assert(makePath(dijkstra(TestGraphs.medium, start), 10) ==
            List(0, 1, 2, 4, 6, 7, 8, 9, 10))
    }

    test("Medium test graph: Path Length: 1-node path") {
        assert(pathLength(List(0) == 0))
    }

    test("Medium test graph: Invalid Path: 0 -> 1 -> 2 -> 9") {
        assert(!(isValidPath(TestGraphs.medium, List(0,1,2,9))))
    }

    test("Medium test graph: Invalid Path: 0 -> 1 -> 6") {
        assert(!(isValidPath(TestGraphs.medium, List(0,1,6))))
    }

    test("isValidPath: 1-node path") {
        assert(isValidPath(TestGraphs.medium, List(6)))
    }
}
