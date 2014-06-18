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

libraryDependencies ++=
  Seq("edu.umass.cs" %% "docker" % "0.2-SNAPSHOT",
      "org.scalatest" %% "scalatest" % "2.2.0" % "test",
      "com.typesafe.slick" %% "slick" % "2.0.2",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe" % "config" % "1.2.1",
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
      "commons-codec" % "commons-codec" % "1.9",
      "commons-io" % "commons-io" % "2.4",
      "org.scala-lang.modules" %% "scala-async" % "0.9.1")
