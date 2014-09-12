name := "submission"

organization := "edu.umass.cs"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

val logbackVersion = "1.0.9"

lazy val logback = "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++=
  Seq("edu.umass.cs" %% "docker" % "0.2",
      "org.scalatest" %% "scalatest" % "2.2.0" % "test",
      "com.typesafe" % "config" % "1.2.1",
      "commons-io" % "commons-io" % "2.4",
      "commons-codec" % "commons-codec" % "1.9",
      "org.apache.commons" % "commons-compress" % "1.8.1",
      "org.scala-lang.modules" %% "scala-async" % "0.9.1",
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "ch.qos.logback" % "logback-core" % logbackVersion,
      "org.yaml" % "snakeyaml" % "1.13",
      "org.fusesource.jansi" % "jansi" % "1.11",
      "com.netflix.rxjava" % "rxjava-scala" % "0.20.0",
      "io.spray" %%  "spray-json" % "1.2.6")

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
