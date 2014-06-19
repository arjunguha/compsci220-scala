import AssemblyKeys._

assemblySettings

name := "submission"

organization := "edu.umass.cs"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

val akkaVersion = "2.3.3"

val logbackVersion = "1.0.9"
lazy val logback = "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime"

libraryDependencies ++=
  Seq("edu.umass.cs" %% "docker" % "0.2-SNAPSHOT",
      "org.scalatest" %% "scalatest" % "2.2.0" % "test",
      "com.typesafe.slick" %% "slick" % "2.0.2",
      "com.typesafe" % "config" % "1.2.1",
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "commons-codec" % "commons-codec" % "1.9",
      "commons-io" % "commons-io" % "2.4",
      "org.scala-lang.modules" %% "scala-async" % "0.9.1",
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "ch.qos.logback" % "logback-core" % logbackVersion)

