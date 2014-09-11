import AssemblyKeys._

lazy val root = project.in(file(".")).aggregate(support, submission)

lazy val support = project.in(file("support-code"))
                          .settings(assemblySettings: _*)
                          .settings(jarName in assembly := "cmpsci220.jar")
                          .settings(assemblyOption in assembly ~= { _.copy(includeScala = false) })


lazy val submission = project.settings(assemblySettings: _*)
                             .settings(assemblySettings: _*)
                             .settings(jarName in assembly := "submission.jar")
                             .settings(assemblyOption in assembly ~= { _.copy(includeScala = false) })

lazy val install = taskKey[Unit]("Installs locally to run on development machine.")

install := {
  import scala.sys.process._
  Seq("/usr/bin/sudo", "mkdir", "-p", "/usr/lib/cs220").!
  Seq("/usr/bin/sudo", "cp", "support-code/bin/scala220", "/usr/bin").!
  Seq("/usr/bin/sudo",  "cp", "support-code/target/scala-2.11/cmpsci220.jar", "/usr/lib/cs220").!
  println("Installed scala220 to /usr")
}

install <<= install.dependsOn(assembly in support, assembly in submission)

lazy val buildDocker = taskKey[Unit]("Builds the Docker image used for testing/grading")

buildDocker := {
  import scala.sys.process._
  Seq("/bin/bash", "-c",
      "cd support-code/docker; sudo docker.io build -t arjunguha/cs220 .").!
}

lazy val compileLib = taskKey[Unit]("Compiles only the library to run locally")

compileLib := ()

compileLib <<= compileLib.dependsOn(compile in Compile)

lazy val compileDocker = taskKey[Unit]("Compiles the library and testing system")

compileDocker := ()

compileDocker <<= compileDocker.dependsOn(buildDocker, install)

lazy val ppa = taskKey[Unit]("Builds and submits the PPA")

ppa := {
  import scala.sys.process._
  Seq("/bin/bash", "-c", "cd ppa; make").!
}

ppa <<= ppa.dependsOn(assembly in support, assembly in submission)


lazy val dockerPush = taskKey[Unit]("Pushes the Docker image to the Docker Registry")

dockerPush := {
  import scala.sys.process._
  Seq("/usr/bin/docker.io", "push", "arjunguha/cs220").!
}

dockerPush <<= dockerPush.dependsOn(buildDocker)

lazy val compileWeb = taskKey[Unit]("Build the website")

compileWeb := {
  import scala.sys.process._
  if (Seq("/bin/bash", "-c", "cd website; make").! != 0) {
    error("Build website failed")
  }
}

compileWeb <<= compileWeb.dependsOn(doc in Compile in support)

lazy val release = taskKey[Unit]("Releases an update to Docker Registry / Ubuntu PPA")

release := ()

release <<= release.dependsOn(ppa, dockerPush)