name := "grader"

organization := "edu.umass.cs"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

libraryDependencies ++=
  Seq("org.scalatest" %% "scalatest" % "2.2.1" % "test",
      "commons-io" % "commons-io" % "2.4",
      "commons-codec" % "commons-codec" % "1.9",
      "org.apache.commons" % "commons-compress" % "1.8.1",
      "org.yaml" % "snakeyaml" % "1.13",
      "org.fusesource.jansi" % "jansi" % "1.11",
      "com.github.tototoshi" %% "scala-csv" % "1.0.0")

// A little hack to dump the CLASSPATH to ./classpath. This lets us run the
// run a standalone executable without building a fat JAR, which is nice
// and fast.

lazy val dumpClasspath = taskKey[Unit]("Dumps the CLASSPATH for scripts")

dumpClasspath <<= (managedClasspath in Compile, baseDirectory) map { (v, base) =>
  import java.nio.file.{Files, Paths}
  val classpath = v.map(_.data).mkString(":")
  Files.write(base.toPath.resolve("classpath"),
              s"CLASSPATH=$classpath".getBytes)
}

compile <<= (compile in Compile) dependsOn dumpClasspath
