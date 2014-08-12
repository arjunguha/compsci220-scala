package cs220.submission.yamltests

import java.util.{List, LinkedList}

private[yamltests] class TestBean {

  private var test : String = null
  private var desc : String = null

  def getTest() : String = {
    assert(test != null)
    test
  }

  def setTest(test : String) : Unit = {
    this.test = test
  }

  def getDesc() : String = {
    assert(desc != null, test)
    desc
  }

  def setDesc(desc : String) : Unit = {
    this.desc = desc
  }

}

private[yamltests] class TestSuiteBean {

  private var tests : List[TestBean] = new LinkedList()
  private var command : List[String] = new LinkedList()

  // Per-test limits in seconds and megabytes
  private var timeLimit : Int = 0
  private var memoryLimit : Int = 0

  private var image : String = null

  def getImage() = {
    assert(image != null)
    image
  }

  def setImage(image : String) : Unit = {
    this.image = image
  }

  def getTests() = tests

  def setTests(tests : List[TestBean]) : Unit = {
    this.tests = tests
  }

  def getCommand() = command

  def setCommand(command : List[String]) : Unit = {
    this.command = command
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

