import cmpsci220._
import cmpsci220.hw.recursion._

// Recursion Lab 2: Folding
// You may work in pairs if you do not have a laptop. Both of you should submit
// the same code file with BOTH of your names on it.
// For this lab, we'll be using the List[A] case class, which you are already
// familiar with.
// Please submit whatever you finish.

// Question 1:
// Write the `sum' function, which takes a list and sums the elements. An empty
// list has the sum of 0.
def sum(lst: List[Int]): Int

test("Sum test: Empty list") {
    assert(sum(List()) == 0)
}

test("Sum test: Short list") {
    assert(sum(List(1,2,3)) == 6)
}

// Write the `product' function, which finds the product of the elements of a
// list. An empty list has a product of 1.
def product(lst: List[Int]): Int

test("Product test: Empty list") {
    assert(product(List()) == 1)
}
test("Product test: Short list") {
    assert(product(List(1,2,3,4)) == 24)
}

// Question 2:
// Rewrite the `sum' function (call it `baseSum') so that it returns a given
// result for empty lists.
def baseSum(onEmpty: Int, lst: List[Int]): Int

test("Sum with base: Empty list") {
    assert(baseSum(-1, List()) == -1)
}

test("Sum with base: Short list") {
    assert(baseSum(-1, List(1,2,3)) == 5)
}

// Question 3:
// Write a function that takes an integer operation, a value for empty lists,
// and a list of integers and returns the result of cumulatively applying the
// operation to all elements of the given list.
def genOp(op: ((Int, Int) => Int), onEmpty: Int, lst: List[Int]): Int

test("genOp: Same as sum") {
    val op = (a: Int, b: Int) => a + b
    assert(genOp(op, 0, List(10, 20, 30)) == sum(List(10,20,30)))
}

test("genOp: Same as product") {
    val op = (a: Int, b: Int) => a * b
    assert(genOp(op, 1, List(10,11,12)) == product(List(10,11,12)))
}

// Question 4:
// Write the `fold' function which takes a function of two arguments, a value for
// empty lists, and a list of the function's input type and returns the result
// of cumulatively applying the function to each element of the list.
def fold[A](op: ((A,A) => A), onEmpty: A, lst: List[A]): A

test("fold: Same as sum") {
    val op = (a: Int, b: Int) => a + b
    assert(fold(op, 0, List(10,20,30)) == sum(List(10,20,30)))
}

test("fold: String test") {
    val op = (a: String, b: String) => a + b
    assert(fold(op, "", List("Hello", ", ", "world", "!")) == "Hello, world!")
}

// Question 5:
// Write the `fold2' function, which takes a function ((A, B) => B), a value for
// empty lists, and a list of the function's input type. It returns the result
// (of type B) of cumulatively applying the function to the elements of the
// list.
def fold2[A,B](op: ((A,B) => B), onEmpty: B, lst: List[A]): B

test("fold2: String lengths") {
    val op = (s: String, x: Int) => x + s.length
    assert(fold2(op, 0, List("Hello", ", ", "world", "!")) == 13)
}
