package cs220.submission.yamltests

private[yamltests] class TestBean {

  private var test : String = null

  def getTest() : String = test

  def setTest(test : String) : Unit = {
    this.test = test
  }

}

private[yamltests] class TestSuiteBean {

  private var filename : String = null
  private var tests : java.util.List[TestBean] = null

  // Per-test limits in seconds and megabytes
  private var timeLimit : Int = -1
  private var memoryLimit : Int = -1

  def getTests() = tests

  def setTests(tests : java.util.List[TestBean]) : Unit = {
    this.tests = tests
  }

  def getFilename() = {
    assert(filename != null)
    filename
  }

  def setFilename(filename : String) : Unit = {
    this.filename = filename
  }

  def getTimeLimit() = {
    assert(timeLimit > 0)
    timeLimit
  }

  def setTimeLimit(timeLimit : Int) : Unit = {
    this.timeLimit = timeLimit
  }

  def getMemoryLimit() = {
    assert(memoryLimit > 0)
    memoryLimit
  }

  def setMemoryLimit(memoryLimit : Int) : Unit = {
    this.memoryLimit = memoryLimit
  }

}

