import cmpsci220._
import cmpsci220.hw.recursion._

def forall[A](f: A => Boolean, lst:List[A]): Boolean = lst match {
	case Empty() => true
	case Cons(head, tail) => f(head) match {
		case true => forall(f, tail)
		case false => false
	}
}

test("forall_test1") {
	def f(n: Int) :Boolean = {
		n % 2 == 0
	}
val lst = List(2,4,6,8,12)
assert(forall(f, lst))
}


test("forall_test1") {
	def f(n: String) :Boolean = {
		n.length % 2 == 0
	}
val lst = List("hello!","Good")
assert(forall(f, lst))
}

def findlast[A] (f: A => Boolean, lst : List[A]) : Option[A] = lst match {
	case Empty() => None[A]()
	case Cons(head, tail) => f(head) match {
		case true => findlast(f, tail) match {
			case None() => Some(head)
 			case Some(x) => Some(x)
		}
		case false => findlast(f, tail)
		}

}

test("findlast_test1") {
	def f(n:Int): Boolean = {
	n % 2 == 0	
	}
val lst = List(200, 100)
assert(findlast(f, lst) == Some(100))
}

test("findlast_test1") {
	def f(n:String): Boolean = {
	n.length % 2 == 0
	}
val lst = List("Welcome","Hey!","Goodbye","Goodbye!")
assert(findlast(f, lst) == Some("Goodbye!"))
}

test("forall_test1") {
	def f(n: String) :Boolean = {
		n.length % 2 == 0
	}
val lst = List("hello!","Good")
assert(forall(f, lst))
}

def findlast2[A] (f: A => Boolean, lst : List[A]) : Option[A] = lst match {
	case Empty() => None[A]()
	case Cons(head, tail) => findlast2(f, tail) match {
					case None() => Some(head)
					case Some(x) => Some(x)
	}
} 

test("findlast2_test1") {
	def f(n:Int): Boolean = {
	n % 2 == 0	
	}
val lst = List(200, 100)
assert(findlast(f, lst) == Some(100))
}

test("findlast2_test1") {
	def f(n:String): Boolean = {
	n.length % 2 == 0
	}
val lst = List("Welcome","Hey!","Goodbye","Goodbye!")
assert(findlast(f, lst) == Some("Goodbye!"))
}
