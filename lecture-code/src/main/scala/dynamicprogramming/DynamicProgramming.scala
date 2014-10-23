package lectures.dynamicprogramming

object Substring {

  def substring(sub: String, str: String) = listContains(sub.toList, str.toList)

  def listContains[A](sublst: List[A], lst: List[A]): Boolean = {
    (sublst, lst) match {
      case (Nil, _) => true
      case (x :: sublst1, y :: lst1) => {
        if (x == y) {
          listContains(sublst1, lst1) || listContains(x :: sublst1, lst1)
        }
        else {
          listContains(x :: sublst1, lst1)
        }
      }
      case (_ :: _, Nil) => false
    }
  }

}

object Subsequence {

  def subsequence[A](xs: List[A], ys: List[A]): Boolean = (xs, ys) match {
    case (Nil, _) => true
    case (_, Nil) => false
    case (x::xs1, y::ys1) => {
      if (x == y) {
        subsequence(xs1, ys1)
      }
      else {
        subsequence(x::xs1, ys1)
      }
    }
  }

  def commonSubsequence[A](xs: List[A], ys: List[A], zs: List[A]): Boolean = {
    subsequence(xs, ys) && subsequence(xs, zs)
  }

}

object RecursiveLCS {

  var counter = 0
  def lcs[A](xs: List[A], ys: List[A]): List[A] = {
    counter = counter + 1
    (xs, ys) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (x :: xs1, y :: ys1) => {
        if (x == y) {
          x :: lcs(xs1, ys1)
        }
        else {
          val tmp1 = lcs(xs, ys1)
          val tmp2 = lcs(xs1, ys)
          if (tmp1.length > tmp2.length) {
            tmp1
          }
          else {
            tmp2
          }
        }
      }
    }
  }
}

object RecursiveLCSLength {

  var counter = 0

  def lcsHelper[A](xs: Array[A], ys: Array[A], i: Int, j: Int): Int = {
    counter = counter + 1
    if (i == 0 || j == 0) {
      if (xs(i) == ys(j)) { 1 } else { 0 }
    }
    else if (xs(i) == ys(j)) {
      1 + lcsHelper(xs, ys, i - 1, j - 1)
    }
    else {
      val tmp1 = lcsHelper(xs, ys, i, j - 1)
      val tmp2 = lcsHelper(xs, ys, i - 1, j)
      if (tmp1 > tmp2) {
        tmp1
      }
      else {
        tmp2
      }
    }
  }

  def lcsLen[A](xs: Array[A], ys: Array[A]): Int = {
    lcsHelper(xs, ys, xs.length - 1, ys.length - 1)
  }

}

object DynamicProgrammingLCSLength {

  var counter = 0

  def lcsHelper[A](table: Array[Array[Int]], xs: Array[A], ys: Array[A], i: Int, j: Int): Int = {
    counter = counter + 1
    // println(i, j)
    if (i == 0 || j == 0) {
      if (xs(i) == ys(j)) { 1 } else { 0 }
    }
    else if (xs(i) == ys(j)) {
      1 + table(i - 1)(j - 1)
    }
    else {
      val tmp1 = table(i)(j - 1)
      val tmp2 = table(i - 1)(j)
      if (tmp1 > tmp2) {
        tmp1
      }
      else {
        tmp2
      }
    }
  }

  def lcsLen[A](xs: Array[A], ys: Array[A]): Int = {
    val table = Array.fill(xs.length) { Array.fill(ys.length) { 0 } }
    for (i <- 0.to(xs.length - 1)) {
      for (j <- 0.to(ys.length - 1)) {
        table(i)(j) = lcsHelper(table, xs, ys, i, j)
      }
    }
    table(xs.length - 1)(ys.length - 1)
  }

}
