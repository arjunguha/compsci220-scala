import edgemaze._
import scala.collection.immutable.Queue

class EdgeGraphSuite extends FunSuite {
    val start = 0
    test("smallGraph Reachable") {
        assert(reachable(TestGraphs.smallGraph, start) ==
            Set(0,1,2,3,4,5,6,7,8,9,10))
    }

    test("smallGraph DFSPath") {
        assert(dfsPath(TestGraphs.smallGraph, start) ==
            Array(-1, 0, 1, 2, 2, 4, 4, 6, 7, 8, 9))
    }

    test("smallGraph DFSPath: Path to node 10") {
        assert(makePath(dfsPath(TestGraphs.smallGraph, start), 10) ==
            List(0, 1, 2, 4, 6, 7, 8, 9, 10))
    }

    test("smallGraph DFSPath: Path to node 10 Length") {
        val p = makePath(dfsPath(TestGraphs.smallGraph, start), 10)
        assert(pathLength(p) == 11)
    }

    test("smallGraph Dijkstra: Path to node 10") {
        assert(makePath(dijkstra(TestGraphs.smallGraph, start), 10) ==
            List(0, 1, 2, 4, 6, 7, 8, 9, 10))
    }

    test("smallGraph Path Length: 1-node path") {
        assert(pathLength(List(0) == 0))
    }

    test("smallGraph Invalid Path: 0 -> 1 -> 2 -> 9") {
        assert(!(isValidPath(TestGraphs.smallGraph, List(0,1,2,9))))
    }

    test("smallGraph Invalid Path: 0 -> 1 -> 6") {
        assert(!(isValidPath(TestGraphs.smallGraph, List(0,1,6))))
    }

    test("isValidPath: 1-node path") {
        assert(isValidPath(TestGraphs.smallGraph, List(6)))
    }
}
