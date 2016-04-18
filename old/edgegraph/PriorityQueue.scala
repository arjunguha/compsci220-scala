package cmpsci220.hw.graph

trait PriorityQueue[A] {

    /**
     * Inserts {@code value} with {@code priority} into the priority queue.
     */
    def insert(priority: Int, value: A): Unit

    /**
     * Removes the lowest-priority element from the priority queue.
     *
     * Returns a pair containing the lowest-priority element and its priority.
     *
     * Throws {@code IllegalArgumentException} if the priority queue is
     * empty.
     */
    def removeMin(): (Int, A)

    /**
     * Changes the priority of {@code value} to {@code priority}.
     *
     * Throws {@code IllegalArgumentException} of {@code value} is not in
     * the priority queue.
     */
    def changePriority(value: A, priority: Int): Unit

    /**
     * Returns {@code true} if the priority queue is empty.
     */
    def isEmpty(): Boolean

}

// private[cmpsci220] class NaivePriorityQueue[A]() extends PriorityQueue[A] {

//   // To avoid inserting the same value at two different priorities
//   val byElt = collection.mutable.Map[A, Int]()

//   val byPriority = collection.mutable.Map[Int, collection.mutable.Set[A]]()

//   var min: Option[Int] = None


//   def insert(priority: Int, value: A): Unit = {
//     if (byElt.contains(value)) {
//       throw new IllegalArgumentException(s"$value is already in the priority queue")
//     }

//     val eltSet = byPriority.get(priority) match {
//       case Some(set) => set
//       case None => {
//         val set = collection.mutable.Set[A]()
//         byPriority += (priority -> set)
//         set
//       }
//     }

//     eltSet += value
//     byElt += (value -> priority)

//     if 
//   }

//   def removeMin(): (Int, A) = {
//     if (byElt.isEmpty) {
//       throw new IllegalArgumentException("empty priority queue")
//     }

//     val minPriority = byPriority.min
//     val minSet = byPriority(minPriority)
//     val aMinElt = minSet.min
//     minSet -= aMinElt
//     if (minSet.isEmpty) {
//       byPriority -= minPriority
//     }
//     byElt -= aMinElt
//     return aMinElt
//   }

//   def changePriority(value: A, priority: Int): Unit = {
//     if (!byElt.contains(value)) {
//       throw new IllegalArgumentException(s"$value is not in the priority queue")
//     }
//     val oldPriority = byElt(value)
//     byElt -= value
//     byElt += (value -> priority)

//     val set = byPriority(oldPriority)
//     set -= value
//     if (set.isEmpty) {
//       byPriority -= oldPriority
//     }
//   }

// }