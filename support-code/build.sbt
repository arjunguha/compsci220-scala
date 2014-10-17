name := "cmpsci220"

organization := "edu.umass.cs"

version := "1.3"

scalaVersion := "2.11.2"

scalacOptions in (Compile,doc) ++=
  Seq("-groups",
      "-implicits")

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

libraryDependencies ++=
  Seq("org.scalatest" %% "scalatest" % "2.2.1" % "test",
      "org.fusesource.jansi" % "jansi" % "1.11",
      "org.apache.commons" % "commons-math3" % "3.3",
      "org.yaml" % "snakeyaml" % "1.13",
      "com.github.tototoshi" %% "scala-csv" % "1.0.0")

// The JavaFX initialization must only run once per JVM instance. Without fork,
// running twice in the same SBT instance will fail.
fork := true

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

// All this boilerplate is to publish to Maven Central or Sonatype Snapshots.

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/cmpsci220/support-code</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>https://github.com/cmpsci220/support-code/blob/master/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:cmpsci220/support-code.git</url>
    <connection>scm:git:git@github.com:cmpsci220/support-code.git</connection>
  </scm>
  <developers>
    <developer>
      <name>Arjun Guha</name>
      <email>arjun@cs.umass.edu</email>
      <url>https://www.cs.umass.edu/~arjun</url>
    </developer>
  </developers>
)