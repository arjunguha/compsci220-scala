package cs220.submission.yamlassignment

import java.util.List

private[yamlassignment] class AssignmentBean {

  private var expected : List[String] = null
  private var boilerplate : List[String] = null

  def getExpected() = expected

  def setExpected(expected : List[String]) : Unit = {
    this.expected = expected
  }

  def getBoilerplate() = boilerplate

  def setBoilerplate(boilerplate : List[String]) : Unit = {
    this.boilerplate = boilerplate
  }

}