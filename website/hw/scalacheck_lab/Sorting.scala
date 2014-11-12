object Sorting {

    def qsort(lst: List[Int]): List[Int] = {
        lst match {
            case Nil => Nil
            case pivot :: rest => {
                val lhs = rest.filter(_ < pivot)
                val rhs = rest.filter(_ >= pivot)
                qsort(lhs) ++ List(pivot) ++ qsort(rhs)
            }
            }
        }

    def isSorted(lst: List[Int]): Boolean = lst match {
        case Nil => true
        case List(x) => true
        case x :: y:: rest => x <= y && isSorted(y :: rest)
    }

}
