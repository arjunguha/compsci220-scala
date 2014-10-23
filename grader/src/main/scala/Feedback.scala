package grader

import java.nio.file._
import scala.collection.JavaConversions._
import scala.sys.process._

object Feedback {

  private def isGraded(p: Path): Boolean = {
    Files.isDirectory(p) && Files.isRegularFile(p.resolve(".graded"))
  }

  // base contains assignemnts with feedback, e.g.,
  //
  // base/student1, base/student2, ...
  //
  // and base/sheet.csv
  //
  // Produces Seq("student1-email" -> 1, "student2-email" -> 1)
  private def countFeedbackReceived(base: String): Seq[(String, Int)] = {
    val b = Paths.get(base)
    val sheet = MoodleSheet(b.resolve("sheet.csv"))
    Files.newDirectoryStream(b)
      .filter(Files.isDirectory(_))
      .map(p => sheet.emailById(p.getFileName.toString).map { mid => (mid, 1) })
      .flatten
      .toSeq
  }

  private def aggregateFeedbackCount(baseDirs: Seq[String]): Map[String, Int] = {
    baseDirs.flatMap(countFeedbackReceived)
      .groupBy(_._1)
      .toMap
      .mapValues(_.map(_._2).sum)
  }

  private def scoreBy(given: Map[String, Int]) (p: (String, Int)): (String, Int) = {
    val (id, n) = p
    (id, n - given.getOrElse(id, 0))
  }

  def prepareForFeedback(groups: Int,
                         perGroup: Int,
                         toFeedback: String,
                         dst: String,
                         pfp: Seq[String]): Unit = {
    val priorFeedbackPaths = (pfp.toSet - dst).toSeq
    val given = aggregateFeedbackCount(priorFeedbackPaths)
    val available = countFeedbackReceived(toFeedback).toSeq
                      .map(scoreBy(given) _)
                      .sortBy(_._2)
                      .reverse // ascending
                      .take(groups * perGroup)
                      .map(_._1) // drop score

    val sheet = MoodleSheet(Paths.get(toFeedback).resolve("sheet.csv"))


    for (dir <- 1.to(groups)) {
      Files.createDirectory(Paths.get(dst).resolve(s"group-$dir"))
    }

   var n = 1
    for (set <- available.grouped(perGroup)) {
      for (email <- set) {
        val id = sheet.idByEmail(email).get
        val srcDir = Paths.get(toFeedback).resolve(id)
        val dstDir = Paths.get(dst).resolve(s"group-$n").resolve(id)
        println(s"$srcDir -> $dstDir")
        Seq("cp", "-r", srcDir.toString, dstDir.toString).!
      }
      n = n + 1
    }
  }

}
