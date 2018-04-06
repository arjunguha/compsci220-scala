import hw.sudoku._

object Solution extends SudokuLike {
  type T = Board
  type B = Map[(Int, Int), List[Int]]

  def main(args: Array[String]): Unit = {
    println("Hello world")
  }

  val vs = for (i <- 0.to(8); j <- 0.to(8)) yield (i, j)

  val peerMap = scala.collection.mutable.Map[(Int, Int), List[(Int, Int)]]()
  def peers(r: Int, c: Int): List[(Int, Int)] = peerMap.getOrElseUpdate((r, c), _peers(r, c))
  def _peers(row: Int, col: Int): List[(Int, Int)] = {
    val rows = (0 to 8).map(c => (row, c))
    val cols = (0 to 8).map(r => (r, col))
    val box = for(i <- 0.to(2); j <- 0.to(2)) yield ((row/3)*3 + i, (col/3)*3 + j)
    (rows ++ cols ++ box).distinct.toList.diff(List((row, col)))
  }

  // The stack contains all the nodes that need to be considered after a deletion from one of
  // their peers.
  type Stack = List[(Int, Int)]

  // Change the value of an index. This triggers a check on all the peers of the index.
  // The check looks at all the peers of an index and removes the values from the
  // domain of the index that are already taken up by the index.
  def placeHelper(m: B, row: Int, col: Int, value: Int): B = {

    require(m((row, col)).contains(value),
      s"row: $row, col: $col -> ${m(row, col)}, does not contain $value"
    )

    val ps: B = Solution.peers(row, col).map({
      case (r, c) => (r, c) -> m(r, c).diff(List(value))
    }).toMap

    // Get a list of all the peers that changed from the initial map to have a single value.
    val changedToOne = peers(row, col).filter({
      case (r, c) => ps(r, c) != m(r, c) && ps(r, c).length == 1
    })

    // Create the updated map
    val updatedMap: B = m ++ ps + ((row, col) -> List(value))

    // On the updated map, make all the nodes in the stack consistent.
    makeConsistent(updatedMap, changedToOne)
  }


  def makeConsistent(map: B, toCheck: Stack): B = {
    // Calculate the peers of the head of the stack (node)
    // Calulate the changed peers of the node, add them to the stack
    // repeat till the stack is empty
    def loop(map: B, toCheck: Stack): B = toCheck match {
      case Nil => map
      case (row, col) :: tl => {
        // If the node has no possible values, return the unsolvable board
        if (map(row, col).length == 0) map
        else {
          require(map(row, col).length == 1, s"($row, $col) does not contain exactly one element")
          val value = map(row, col).head

          val ps: B = Solution.peers(row, col).map({
            case (r, c) => (r, c) -> map(r, c).diff(List(value))
          }).toMap

          // Get a list of all the peers that changed from the initial map to have a single value.
          val changedToOne = peers(row, col).filter({
            case (r, c) => ps(r, c) != map(r, c) && ps(r, c).length == 1
          }).toList

          // Create the updated map
          val updatedMap = map ++ ps + ((row, col) -> List(value))
          loop(updatedMap, tl ::: changedToOne)
        }
      }
    }

    loop(map, toCheck)
  }

  def parse(str: String): Board = {
    val zipped = vs.toList.zip(str.trim.toList).filter( _._2 != '.').map(r => (r._1, r._2.asDigit))

    val initBoard: B = vs.map(i => i -> (1 to 9).toList).toMap

    val map = zipped.foldLeft(initBoard)({
      case (acc, ((r, c), v)) => placeHelper(acc, r, c, v)
    })
    new Board(map)
  }
}

class Board(val available: Map[(Int, Int), List[Int]]) extends BoardLike[Board] {

  def _isSolved: Boolean = {
    Solution.vs.forall(i => available(i).size == 1)
  }

  def _isUnsolvable: Boolean = {
    Solution.vs.exists(i => available(i).isEmpty)
  }

  lazy val isUnsolvable = _isUnsolvable
  lazy val isSolved = _isSolved

  override def equals(that: Any) = that match {
    case b: Board => available == b.available
    case _ => false
  }

  def availableValuesAt(row: Int, col: Int): List[Int] = {
    available.getOrElse((row, col), 1.to(9).toList)
  }

  def valueAt(row: Int, col: Int): Option[Int] = available.get((row, col)) match {
    case None => None
    case Some(l) => if (l.length == 1) Some(l.head) else None
  }

  def place(row: Int, col: Int, value: Int): Board = {
    val completeMap = Solution.vs.map({
      case (r, c) => (r, c) -> availableValuesAt(r, c)
    }).toMap
    new Board(Solution.placeHelper(completeMap, row, col, value))
  }


  // Can return any Iterable (probably use Stream)
  def _nextStates: Stream[Board] = {
    if(isUnsolvable) Stream()
    else {
      Solution.vs.filter({ case (i, j) => available(i, j).size > 1 }).
        sortBy(x => available(x._1, x._2).length).toStream.flatMap({
        case (r, c) => available((r, c)).map(v => new Board(available).place(r, c, v))
      })
    }
  }

  lazy val nextStates = _nextStates

  def solve: Option[Board] = {
    if(isSolved) Some(this)
    else if(isUnsolvable || nextStates.length == 0) None
    else {
      nextStates.map(_.solve).filter(_.isDefined).headOption.flatten
    }
  }
}
