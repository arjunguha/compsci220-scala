// We are going to define a function to find subsequences in lists and arrays. But, it is easier to write a string
// and convert it using .toList or .toArray

"hello world".toList
"hello world".toArray


def subseq[A](xs: List[A], ys: List[A]): Boolean = (xs, ys) match {
  case (Nil, _) => true
  case (_, Nil) => false
  case (x::xs1, y::ys1) => {
    if (x == y) {
      subseq(xs1, ys1)
    }
    else {
      subseq(xs, ys1)
    }
  }
}

println("Should all be true:")
subseq("abc".toList, "1a2b3c".toList)
subseq("abcd".toList, "1a2b3c".toList) == false
subseq("abc".toList, "aabc".toList)
subseq("aabc".toList, "abc".toList) == false
// This lecture is about common subsequences:

def commonSubseq[A](xs: List[A], ys: List[A], zs: List[A]): Boolean = {
  subseq(xs, ys) && subseq(xs, zs)
}

// We are going to write a several functions to calculuate the longest common subsequence.
// Here are two important properties of the function:
// 1. commonSubseq(lcs(xs, ys), xs, ys)
// 2. There is no list zs, where zs.length > lcs(xs, ys).length and commonSubseq(zs, xs, ys)

// Our first version is recursive. We also add a counter to count the number of recursive calls:


def lcsRec[A](xs: List[A], ys: List[A]): List[A] = {
  var counter = 0

  def lcs(xs: List[A], ys: List[A]): List[A] = {
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

  val result = lcs(xs, ys)
  println(s"$counter calls to lcs")
  result
}

// The number of calls grows very, very quickly
lcsRec("aabc".toList, "abc".toList)
lcsRec("xabzcy".toList, "12a4b39c".toList)
// Here is another version of the above program that prints the recursive calls:
def lcsRecNoisy[A](xs: List[A], ys: List[A]): List[A] = {
  var counter = 0

  def lcs(xs: List[A], ys: List[A]): List[A] = {
    println(s"lcs($xs, $ys)")
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

  val result = lcs(xs, ys)
  println(s"$counter calls to lcs")
  result
}

// If you try different arguments, you'll find that there are many repeated recursive
// calls. Let's try to avoid this kind of needness recomputation. A naive solution
// would be to store the result of every computation in a hashtable, but we'll look
// at a cleaner and more efficient approach.
lcsRecNoisy("aabc".toList, "abc".toList)


// First, let's write a function that calculates the length of the longest common subsequence
// instead ot the LCS itself. (It is easy to recover the actual LCS.) We'll also change the
// function to work with arrays instead of lists.
def lcsLenRec[A](xs: Array[A], ys: Array[A]): Int = {
  var counter = 0
  def f[A](i: Int, j: Int): Int = {
    counter = counter + 1
    if (i == 0 || j == 0) {
      0
    }
    else if (xs(i - 1) == ys(j - 1)) {
      1 + f(i - 1, j - 1)
    }
    else {
      math.max(f(i, j - 1), f(i - 1, j))
    }
  }
  val result = f(xs.length, ys.length)
  println(s"$counter calls in lcsLenRec")
  result
}

lcsLenRec("1a2b3c4".toArray, "XaYbZcW".toArray)

// Finally, instead of recurring, we build a 2D Array to store values instead of recurring


def lcsLen[A](xs: Array[A], ys: Array[A]): Int = {
  val m = xs.length
  val n = ys.length
  val table = Array.fill(m + 1) { Array.fill(n + 1) { 0 } }

  def f[A](i: Int, j: Int): Int = {
    if (i == 0 || j == 0) {
      0
    }
    else if (xs(i - 1) == ys(j - 1)) {
      1 + table(i - 1)(j - 1)
    }
    else {
      math.max(table(i)(j - 1), table(i - 1)(j))
    }
  }
  for (i <- 0.to(m);
       j <- 0.to(n)) {
    table(i)(j) = f(i, j)
  }

  table(m)(n)
}

// The number of invocations of f is exactly the product of the lengths of the two lists:
println("Should be 3:")
lcsLen("aabc".toArray, "abc".toArray)
lcsLen("xabzcy".toArray, "12a4b39c".toArray)
