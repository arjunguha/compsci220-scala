package grading

class TestSuite extends org.scalatest.FunSuite {

  test("Private IP is parsed correctly") {
    val txt = """{"networkInterface":[{"accessConfiguration":[{"externalIp":"104.196.61.192","type":"ONE_TO_ONE_NAT"}],"ip":"10.240.0.3","network":"projects/780349874596/networks/default"}]}"""
    import upickle.default._
    import InstanceMetadata._
    assert(read[Network](txt).networkInterface.head.ip == "10.240.0.3")
  }
}
