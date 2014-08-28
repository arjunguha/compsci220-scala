import cs220.submission.sandbox.{Sandbox, Complete, DidNotFinish, SandboxResult}
import scala.concurrent._
import scala.concurrent.duration._

class SandboxSuite extends SandboxFixture {

  // test("no network in sandbox (ping test)") { sandbox =>
  //   val result = sandbox.test("/bin/ping", "-c", "1", "google.com")
  //   whenReady(result) {
  //     case Complete(2, _, _) => ()
  //     case other => fail(s"unexpected result: $other")
  //   }
  // }

  // test("no network in sandbox (ifconfig test)") { sandbox =>
  //   val result = sandbox.test("/sbin/ifconfig")
  //   whenReady(result) {
  //     case Complete(0, stdout, _) =>
  //       assert("Ethernet".r.findFirstIn(stdout).isEmpty)
  //     case other => fail(s"unexpected result: $other")
  //   }
  // }

  // test("timeouts work") { sandbox =>
  //   val result = sandbox.test("/bin/bash", "-c", "echo Start; while true; do true; done")

  //   whenReady(result) {
  //     case DidNotFinish("Start\n", "") => ()
  //     case other => fail(s"expected timeout, but got $other")
  //   }
  // }

  test("/data should mount") { sandbox =>
    Await.result(sandbox.test("/bin/ls", "/data"), 30.second) match {
      case Complete(0, stdout, "") => {
        assert(stdout.indexOfSlice("expected.txt") != -1)
      }
      case other => fail(other.toString)
    }
  }

  // Depends on /data mounting
  test("exceeding memory limit") { sandbox =>
    Await.result(sandbox.test("/data/memoryFail"), 30.second) match {
      case Complete(-1, _, _) => ()
      case other => fail(other.toString)
    }
  }

  test("staying in memory limit") { sandbox =>
    Await.result(sandbox.test("/data/memorySuccess"), 30.second) match {
      case Complete(0, _, _) => ()
      case other => fail(other.toString)
    }
  }


}
