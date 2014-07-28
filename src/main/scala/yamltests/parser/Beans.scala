package cs220.submission.yamltests.parser

private[parser] class TestBean {

  private var test : String = null

  def getTest() : String = test

  def setTest(test : String) : Unit = {
    this.test = test
  }

}

private[parser] class TestSuiteBean {

  private var tests : java.util.List[TestBean] = null

  def getTests() = tests

  def setTests(tests : java.util.List[TestBean]) : Unit = {
    this.tests = tests
  }

}

