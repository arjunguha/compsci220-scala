package grading

object GradeHW1 {

  import scala.concurrent._
  import scala.concurrent.duration._
  import Messages._
  import edu.umass.cs.zip._

  def main(): Unit = {
    val scripting = new grading.Scripting("10.8.0.6")
    import java.nio.file._
    import scripting._

    import scripting.system.dispatcher

    object Test {

      def apply(key: String, score: Int, body: => Future[Result]): Test = {
        new Test(key, score, body, Nil)
      }

    }

    class Test(key: String, score: Int, body: => Future[Result], var children: List[Test]) {

      def parentFailed(parent: String, map: Map[String, Result]): Map[String, Result] = {
        children.foldLeft(map + (key -> DidNotRun(score, parent))) {
          case (map, child) => {
            child.parentFailed(parent, map)
          }
        }
      }

      private def combineResults(m1: Map[String, Result], m2: Map[String, Result]) = {
        val keys = (m1.keySet ++ m2.keySet).toList
        keys.map(k => (m1.get(k), m2.get(k)) match {
          case (Some(Passed(score)), _) => k -> Passed(score)
          case (_, Some(Passed(score))) => k -> Passed(score)
          case (Some(failed@Failed(_, _, _, _)), _) => k -> failed
          case (_, Some(failed@Failed(_, _, _, _))) => k -> failed
          case (Some(v@DidNotRun(_, _)), _) => k -> v
          case (_, Some(v@DidNotRun(_, _))) => k -> v
          case (None, None) => throw new Exception("impossible")
        }).toMap
      }



      def run(map: Map[String, Result]): Future[Map[String, Result]] = {
        map.get(key) match {
          case Some(failed@Failed(_, _, _, _)) => {
            Future(children.foldLeft(map + (key -> failed)) {
              case (map, child) => {
                child.parentFailed(key, map)
              }
            })
          }
          case Some(passed@Passed(_)) => {
            val m = map + (key -> passed)
            Future.sequence(children.map(child => child.run(m)))
            .map(lst => lst.foldLeft(m)(combineResults))
          }
          case Some(didNotRun@DidNotRun(_, _)) => {
            println("ERROR SHOULD NOT HAVE HAPPENED")
            assert(false)
            null
          }
          case None => {
            body.flatMap(r => this.run(map + (key -> r)))
          }
        }
      }

      def requiredFor(test: Test) = {
        new Test(key, score, body, test :: children)
      }

      def runChild(label: String, dir: Path, score: Int, body: String) = {
        this.requiredFor(Test(label, score, {
          val zip = ZipBuilder()
           .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
           .add(s"object Grading extends App { import Lecture1._; $body }".getBytes, "src/main/scala/GradingMain.scala")
           .build()
          scripting.run(45, Seq("sbt", "run"), zip, label = s"$label for $dir").map(_.toResultByCode(score))
        }))
      }

      def addChild(child: Test): Unit = {
        children = child :: children
      }

    }

    def testCompile(dir: Path, body: String) = {
      val zip = ZipBuilder()
       .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
       .add(s"object Grading extends App { import Lecture1._; $body }".getBytes, "src/main/scala/GradingMain.scala")
       .build()
      run(45, Seq("sbt", "compile"), zip).map(_.toResultByCode(10))
    }

    def testRun(dir: Path, desc: String, score: Int, bodyStr: String): Test = {
      Test(desc, score, {
        val zip = ZipBuilder()
         .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
         .add(s"object Grading extends App { import Lecture1._; $bodyStr }".getBytes, "src/main/scala/GradingMain.scala")
         .build()
        scripting.run(45, Seq("sbt", "run"), zip, label = s"$bodyStr for $dir").map(_.toResultByCode(score))
      })
    }

    val lst = Scripting.assignments("hw1").map(dir => {
      updateState(dir.resolve("grading.json")) { case report =>

      val removeZeroes = Test("removeZeroes have the right type?", 10, {
          testCompile(dir, "val x: List[Int] = Lecture1.removeZeroes(List[Int]())")
      })

      removeZeroes.addChild(
        testRun(dir, "removeZeroes with only zeroes", 10,
          """assert(removeZeroes(List(0, 0, 0, 0)) == Nil)"""))

      removeZeroes.addChild(
        testRun(dir, "removeZeroes preserves non-zero values", 10,
          """assert(removeZeroes(List(0, 1, 0, 2, 0)) == List(1, 2))"""))

      val countEvens = Test("Does countEvens have the right type?", 10, {
        testCompile(dir, "val x: Int = countEvens(List[Int]())")
      })

      countEvens.addChild(testRun(dir, "countEvens on an empty list", 10, """assert(countEvens(Nil) == 0)"""))

      countEvens.addChild(testRun(dir, "countEvens with no even numbers", 10, """assert(countEvens(List(1, 3, 5, 7)) == 0)"""))

      countEvens.addChild(testRun(dir, "countEvens with only even numbers", 10, """assert(countEvens(List(2, 4, 6, 8)) == 4)"""))

      countEvens.addChild(testRun(dir, "countEvens with a mixed list of numbers", 10,
        """assert(countEvens(List(1, 3, 2, 5, 7, 2, 9, 2)) == 3)"""))

      val removeAlternating = Test("Does removeAlternating have the right type?", 10, {
          testCompile(dir, "val x: List[String] = removeAlternating(List[String]())")
        })

      removeAlternating.addChild(testRun(dir, "removeAlternating on an empty list", 10, """assert(removeAlternating(Nil) == Nil)"""))
      removeAlternating.addChild(testRun(dir, "removeAlternating on a singleton list", 10, """assert(removeAlternating("1" :: Nil) == "1" :: Nil)"""))
      removeAlternating.addChild(testRun(dir, "removeAlternating on a long list", 10, """assert(removeAlternating(List("1","2","3","4","5")) == List("1","3","5"))"""))

      val isAscending = Test("Does isAscending have the right type?", 10, {
          testCompile(dir, "val x: Boolean = isAscending(List[Int]())")
      })

      isAscending.addChild(testRun(dir, "isAscending on an empty list", 10, """assert(isAscending(Nil) == true)"""))
      isAscending.addChild(testRun(dir, "isAscending on an ascending list", 10, """assert(isAscending(List(1, 3, 7, 100)) == true)"""))
      isAscending.addChild(testRun(dir, "isAscending on a non-ascending list", 10, """assert(isAscending(List(1, 3, 5, 2)) == false)"""))
      isAscending.addChild(testRun(dir, "isAscending on a list with duplicates", 10, """assert(isAscending(List(1, 1, 1, 1, 2)) == true)"""))

      val addSub = Test("Does addSub have the right type?", 10, {
        testCompile(dir, "val x: Int = addSub(List[Int]())")
      })

      addSub.addChild(testRun(dir, "addSub on an empty list", 10, """assert(addSub(Nil) == 0)"""))
      addSub.addChild(testRun(dir, "addSub on a singleton list", 10, """assert(addSub(List(10)) == 10)"""))
      addSub.addChild(testRun(dir, "addSub on a three-element list", 10, """assert(addSub(List(10, 5, 1)) == 6)"""))

      val fromTo = Test("Does fromTo have the right type?", 10, {
          testCompile(dir, "val x: List[Int] = fromTo(0, 1)")
        })

      fromTo.addChild(testRun(dir, "fromTo(10, 11)", 10, """assert(fromTo(10, 11) == List(10))"""))
      fromTo.addChild(testRun(dir, "fromTo(5, 10)", 10, """assert(fromTo(5, 10) == List(5, 6, 7, 8, 9))"""))

      val insertOrdered = Test("Does insertOrdered have the right type?", 10, {
        testCompile(dir, "val x: List[Int] = insertOrdered(0, List[Int]())")
      })

      insertOrdered.addChild(testRun(dir, "insertOrdered into empty list", 10,
        """assert(insertOrdered(10, Nil) == List(10))"""))
      insertOrdered.addChild(testRun(dir, "insertOrdered into head", 10,
        """assert(insertOrdered(5, List(6, 7, 8)) == List(5,6,7,8))"""))
      insertOrdered.addChild(testRun(dir, "insertOrdered into last position", 10,
          """assert(insertOrdered(200, List(6, 7, 8)) == List(6,7,8,200))"""))
      insertOrdered.addChild(testRun(dir, "insertOrdered into mid position", 10,
          """assert(insertOrdered(7, List(6, 8)) == List(6,7,8))"""))

      val sort = Test("Does sort have the right type?", 10, {
        testCompile(dir, "val x: List[Int] = sort(List[Int]())")
      })

      sort.addChild(testRun(dir, "sort an empty list", 10, """assert(sort(Nil) == Nil)"""))
      sort.addChild(testRun(dir, "sort non-empty list", 10, """assert(sort(List(5, 4, 1, 2, 3)) == List(1, 2, 3, 4, 5))"""))

      val tests = Test("Does it compile", 0, {
           val zip = ZipBuilder()
           .filterAdd(dir, "", p => p.filename.endsWith(".scala") && p.filename != "GradingMain.scala")
           .build()
          run(45, Seq("sbt", "compile"), zip).map(_.toResultByCode(10))
        })
       .requiredFor(Test("Does sumDouble have the right type?", 10, {
          testCompile(dir, "val x:Int = Lecture1.sumDouble(List[Int]())")
        })
        .runChild("sumDouble(Nil)", dir, 10, "assert(sumDouble(Nil) == 0)")
        .runChild("sumDouble on a non-empty list", dir, 10, "assert(sumDouble(List(2, 2, 3, 7)) == 28)")
       )
       .requiredFor(removeZeroes)
       .requiredFor(countEvens)
       .requiredFor(removeAlternating)
       .requiredFor(isAscending)
       .requiredFor(addSub)
       .requiredFor(fromTo)
       .requiredFor(insertOrdered)
       .requiredFor(sort)


       tests.run(report.tests
       ).map(x => Rubric(x))
     }
    })
    Await.result(Future.sequence(lst), Duration.Inf)

    val gradeRegex = """Percentage: (\d+)%""".r
    val grades = MoodleSheet("hw1/moodle.csv").fill(id => {
      val reportPath = Paths.get(s"hw1/$id/report.txt")
      if (Files.exists(reportPath)) {
        val feedback = new String (Files.readAllBytes(reportPath))
        val grade = gradeRegex.findFirstMatchIn(feedback).get.group(1).toInt
        (grade, feedback)
      }
      else {
        (0, "Late submission. Will be graded later")
      }
    })
    grades.saveAs("hw1/moodle.csv")
    println("Done grading")
    //val r = run("gcr.io/umass-cmpsci220/student", 30, Seq("/bin/ls", "/"))
    //println(Await.result(r, Duration.Inf))
    scripting.system.terminate()
  }

}
