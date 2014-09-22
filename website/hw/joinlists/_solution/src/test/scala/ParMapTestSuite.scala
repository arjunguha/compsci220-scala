import cmpsci220.hw.joinlists._

import Solution._
import scala.concurrent._
import duration._


class ParMapTestSuite extends org.scalatest.FunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  // def ack(m: BigInt, n: BigInt): BigInt = {
  //   if (m==0) n+1
  //   else if (n==0) ack(m-1, 1)
  //   else ack(m-1, ack(m, n-1))
  // }

  // test("timing parmap") {
  //   val lst = fromList(List(7, 8, 9))
  //   val start = System.currentTimeMillis()

  //   println(Await.result(parMap((n: Int) => ack(3, n), lst), Duration.Inf))
  //   val stop = System.currentTimeMillis()
  //   println(stop - start)
  // }

  // test("timing map") {
  //   val lst = fromList(List(7, 8, 9))
  //   val start = System.currentTimeMillis()
  //   println(map((n: Int) => ack(3, n), lst))
  //   val stop = System.currentTimeMillis()
  //   println(stop - start)

  // }

}
