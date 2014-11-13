import org.scalatest._
import org.scalatest.prop._
import Sorting._
import JoinList._
import org.scalacheck._
import Gen._
import Arbitrary.arbitrary

class ScalaCheckTests extends FunSuite with GeneratorDrivenPropertyChecks {

    // 1. Small Integers generator
    val smallIntegers = Gen.choose(50, 150)

    // 2. Generator for lists of small integers
    val smallIntLists = Gen.containerOf[List, Int](smallIntegers)

    // 3. Test that all numbers in list < 200
    test("All numbers are less than 200") {
        forAll(smallIntLists) { (list: List[Int]) =>
            assert(list.forall{_ < 200})
        }
    }

    // 4. Count how many numbers in a list are less than 100
    test("How many are less than 100") {
        forAll(smallIntLists) { (list: List[Int]) =>
            assert(list.count{_<100} == list.filter{_<100}.length)
        }
    }

    // 5. Make a JoinList generator
    // 3 types: Empty, Singleton, or Join
    val genEmpty = Gen.const(Empty)
    val genSingleton = Singleton(arbitrary[Int])

    val genJoin = for {
        list <- arbitrary[List[Int]]
    } yield fromList(list)

    val genBigJoinList = for {
        list <- arbitrary[List[List[Int]]]
    } yield fromList(list.map(fromList))

    // 6. Recreate the testse from class
    test("qsort checking") {
        forAll(genJoin) { jlist =>
            val sortedList = qsort(toList(jlist))
            assert(isSorted(sortedList))
            assert(sortedList.length == toList(jlist).length)
        }
    }

    def isEven(n: Int): Boolean = n % 2 == 0

    test("filter for joinlists") {
        forAll(genJoin) { jlist =>
            assert(
                toList(filter(isEven, jlist)) == toList(jlist).filter(isEven)
            )
        }
    }

    test("flatten for joinlists") {
        forAll(genBigJoinList) { jlist: JoinList[JoinList[Int]] =>
            val lst: List[List[Int]] = toList(jlist).map(toList)
            assert(toList(flatten(jlist)) == lst.flatten)
        }
    }
}
