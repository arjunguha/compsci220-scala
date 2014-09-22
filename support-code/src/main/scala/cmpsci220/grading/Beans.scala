package cmpsci220.grading

import java.util.{List, LinkedList}

private[grading] class TestResultBean {

  private var criterion: String = null
  private var score: Int = 0
  private var maxScore: Int = 0

  def getCriterion(): String = {
    assert(criterion != null)
    criterion
  }

  def setCriterion(criterion: String): Unit = { this.criterion = criterion }

  def getScore() = score

  def setScore(score: Int): Unit = { this.score = score }

  def getMaxScore() = maxScore

  def setMaxScore(maxScore: Int): Unit = { this.maxScore = maxScore }

}

private[grading] class CumulativeBean {
  private var score: Int = 0
  private var maxScore: Int = 0

  def getScore() = score

  def setScore(score: Int): Unit = { this.score = score }

  def getMaxScore() = maxScore

  def setMaxScore(maxScore: Int): Unit = { this.maxScore = maxScore }

}

private[grading] class FeedbackBean {

  private var rubric = new LinkedList[TestResultBean]()
  private var time: String = "not yet graded"
  private var cumulative: CumulativeBean = new CumulativeBean()

  def getRubric() = {
    assert(rubric != null)
    rubric
  }

  def setRubric(rubric: LinkedList[TestResultBean]): Unit = {
    this.rubric = rubric
  }

  def getTime() = {
    assert(time != null)
    time
  }

  def setTime(time: String): Unit = {
    this.time = time
  }

  def getCumulative(): CumulativeBean = {
    assert(cumulative != null)
    cumulative
  }

  def setCumulative(cumulative: CumulativeBean): Unit = {
    this.cumulative = cumulative
  }


}
