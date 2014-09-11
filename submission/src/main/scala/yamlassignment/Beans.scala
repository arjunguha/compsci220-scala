package plasma.grader.yamlassignment

import java.util.{List, LinkedList}

private[yamlassignment] class AssignmentBean {

  private var expected : List[String] = new LinkedList()
  private var boilerplate : List[String] = new LinkedList()

  def getExpected() = expected

  def setExpected(expected : List[String]) : Unit = {
    this.expected = expected
  }

  def getBoilerplate() = boilerplate

  def setBoilerplate(boilerplate : List[String]) : Unit = {
    this.boilerplate = boilerplate
  }

}