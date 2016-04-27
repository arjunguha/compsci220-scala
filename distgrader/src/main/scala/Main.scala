package grading

import java.net.InetAddress

object Main extends App {

  val localhost = InetAddress.getLocalHost

  import scala.concurrent._
  import scala.concurrent.duration._
  import akka.actor.{Props, ActorSystem}

  sealed trait Command
  case object Grade extends Command
  case object Extract extends Command
  case object Worker extends Command
  case object InitImage extends Command
  case object UploadWorker extends Command
  case object StartController extends Command
  case object StartWorker extends Command
  case object Summarize extends Command
  case object Forget extends Command
  case object UpdateCSV extends Command
  case object LateDays extends Command
  case object Attendance extends Command

  case class Config(command: Command, opts: Map[String, String], ints: Map[String, Int])

  val parser = new scopt.OptionParser[Config]("grader") {

    def key(name: String) = {
      opt[String](name).required
        .action((x, c)  => c.copy(opts = c.opts + (name -> x)))
    }

    def int(name: String) = {
      opt[Int](name).required
        .action((x, c)  => c.copy(ints = c.ints + (name -> x)))
    }

    cmd("late").action((_, cfg) => cfg.copy(command = LateDays)).children(key("root"))

    cmd("attendance").action((_, cfg) => cfg.copy(command = Attendance)).children(key("root"))

    cmd("grade")
      .action((_, cfg) => cfg.copy(command = Grade))
      .text("Grade an assignment")
      .children(key("name"), key("root"), key("controller"))

    cmd("extract")
      .action((_, cfg) => cfg.copy(command = Extract))
     .children(key("src"), key("dst"))

    cmd("upload-worker")
      .action((_, cfg) => cfg.copy(command = UploadWorker))


    cmd("worker")
      .action((_, cfg) => cfg.copy(command = Worker))
      .text("Start a worker process on this machine (requires Docker locally)")

    cmd("start-worker")
      .action((_, cfg) => cfg.copy(command = StartWorker))
      .text("Start workers on Google Compute Engine")
      .children(key("prefix"), key("controller"), int("n"))

    cmd("summarize")
      .action((_, cfg) => cfg.copy(command = Summarize))
      .children(key("root"))
    cmd("forget")
      .action((_, cfg) => cfg.copy(command = Forget))
      .text("Forget that we graded the specified item")
      .children(key("root"), key("label"))
    cmd("update-csv")
      .action((_, cfg) => cfg.copy(command = UpdateCSV))
      .text("Updates moodle.csv with grades")
      .children(key("root"))

  }

  parser.parse(args, Config(Grade, Map(), Map())) match {
    case None => {
      println(parser.usage)
      System.exit(1)
    }
    case Some(config) =>{
      val opts = config.opts
      val ints = config.ints
      config.command match {
        case Grade => {
          val root = opts("root")
          val ip = opts("controller")
          val framework = opts("name") match {
            case "hw1" => new HW1Grading(root, ip)
            case "hw2" => new HW2Grading(root, ip)
            case "hw3" => new HW3Grading(root, ip)
            case "hw4" => new HW4Grading(root, ip)
            case "discussion1" => new Discussion1Grading(root, ip)
            case "tictactoe" => new GradingTicTacToe(root, ip)
            case "generics" => new GradeGenerics(root, ip)
            case "sudoku" => new GradeSudoku(root, ip)
            case "implicits" => new GradeImplicits(root, ip)
            case "regexes" => new GradeRegexes(root, ip)
            case "parsing" => new GradeParsing(root, ip)
            case _ => {
              println("Unknown assignment")
              throw new Exception("Unknown assignment")

            }
          }
          SBTTesting.distributedTesting(framework)

        }
        case Worker => {
          import akka.actor.{Props, ActorSystem}
          val ip = Await.result(InstanceMetadata.getPrivateIP(ExecutionContext.Implicits.global), 15.seconds)
          val config = AkkaInit.remotingConfig(ip, 5000)
          val system = ActorSystem("worker", config)
          val workerActor = system.actorOf(Props[WorkerActor], name=s"worker")
        }
        case InitImage => CreateImage.init()
        case UploadWorker => CreateImage.uploadWorker()
        case StartController => {
          val system = ActorSystem("controller", AkkaInit.remotingConfig(opts("ip"), 5000))
          val controllerActor = system.actorOf(Props[ControllerActor], name="controller")
        }
        case StartWorker => {
          import ExecutionContext.Implicits.global
          val name = opts("prefix")
          val controllerHost = opts("controller")
          val n = ints("n")
          val lst = 0.until(n).map(n => CreateImage.createWorker(s"$name-$n", controllerHost))
          Await.result(Future.sequence(lst), Duration.Inf)
        }
        case Extract => {
          val src = opts("src")
          val dst = opts("dst")
          Scripting.extract(src, dst)
        }
        case Forget => {
          import scala.concurrent.ExecutionContext.Implicits.global
          val label = opts("label")
          var i = 0
          for (dir <- Scripting.assignments(opts("root"))) {
            Scripting.updateState(dir.resolve("grading.json")) { rubric =>
              if (rubric.tests.contains(label)) {
                i = i + 1
              }
              Future { rubric - label }
            }
          }
          println(s"Forgot $i tests.")
        }
        case UpdateCSV => {
          import java.nio.file._
          val dir = opts("root")
          val gradeRegex = """Percentage: (\d+)%""".r
          val grades = MoodleSheet(s"$dir/moodle.csv").fill(id => {
            val reportPath = Paths.get(s"$dir/$id/report.txt")
            if (Files.exists(reportPath)) {
              val feedback = new String (Files.readAllBytes(reportPath))
              val grade = gradeRegex.findFirstMatchIn(feedback).get.group(1).toInt
              (grade, feedback)
            }
            else {
              (0, "Not graded")
            }
          })
          grades.saveAs(s"$dir/moodle.csv")
          println(s"Updated $dir/moodle.csv")
        }
        case Summarize => {
          import Scripting._
          import Messages.{Passed, Failed, DidNotRun}
          val root = opts("root")
          val byTest = assignments(root).map(p => readRubric(p.resolve("grading.json")).tests.toList)
            .flatten.groupBy(_._1)

          for ((test, results) <- byTest) {
            val passed = results.filter(_._2.isInstanceOf[Passed]).size
            val failed = results.filter(_._2.isInstanceOf[Failed]).size
            val didNotRun = results.filter(_._2.isInstanceOf[DidNotRun]).size
            val rate = ((passed.toDouble / results.length.toDouble) * 100.0).toInt
            println(s"$test\n  $passed passed, $failed failed, $didNotRun did not run (success rate: $rate%)")
          }
        }
        case LateDays => {
          grading.LateDays.fromDirectory(opts("root"))
        }
        case Attendance => {
          grading.Attendance.fromDirectory(opts("root"))
        }

      }
    }
  }

}
