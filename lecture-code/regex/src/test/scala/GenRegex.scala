import cmpsci220.regex._
import org.scalacheck._

object GenRegex {

  private def genChar: Gen[Regex] = for {
    ch <- Gen.oneOf('a', 'b', 'c', 'd', 'e', 'f')
  } yield Character(ch)

  private def genStar(size: Int): Gen[Regex] = for {
    p <- genSizedRegex(size - 1)
  } yield Star(p)

  private def genAlt(size: Int): Gen[Regex] = for {
    p <- genSizedRegex(size / 2)
    q <- genSizedRegex(size / 2)
  } yield Alt(p, q)

  private def genSeq(size: Int): Gen[Regex] = for {
    p <- genSizedRegex(size / 2)
    q <- genSizedRegex(size / 2)
  } yield Seq(p, q)


  private def genSizedRegex(size: Int): Gen[Regex] = {
    if (size == 0) {
      genChar
    }
    else {
      Gen.oneOf(genSeq(size), genAlt(size), genStar(size))
    }
  }

  def genRegex = Gen.sized(genSizedRegex)

}


