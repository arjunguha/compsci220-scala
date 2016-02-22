name := "grading"
organization := "edu.umass.cs"
version := "0.1"
scalaVersion in ThisBuild := "2.11.7"
scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xfatal-warnings"
)

resolvers in ThisBuild ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Repository" at "http://repo.akka.io/snapshots/",
  "PLASMA" at "https://dl.bintray.com/plasma-umass/maven"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-remote" % "2.4.1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.1",
  "org.slf4j" % "slf4j-simple" % "1.7.12",
  "com.ning" % "async-http-client" % "1.9.31",
  "org.scalariform" %% "scalariform" % "0.1.8",
  "com.lihaoyi" %% "upickle" % "0.3.7",
  "io.spray" %%  "spray-json" % "1.3.2",
  "com.spotify" % "docker-client" % "3.5.9",
  "commons-io" % "commons-io" % "2.4",
  "com.github.tototoshi" %% "scala-csv" % "1.0.0",
  "edu.umass.cs" %% "zip" % "1.2.1",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.google.api-client" % "google-api-client" % "1.21.0",
  "com.google.apis" % "google-api-services-compute" % "v1-rev88-1.21.0",
  "com.google.apis" % "google-api-services-storage" % "v1-rev57-1.21.0",
  "com.spotify" % "docker-client" % "3.5.9",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "commons-codec" % "commons-codec" % "1.10",
  "com.github.scopt" %% "scopt" % "3.3.0")
  .map(_.exclude("com.google.guava", "guava-jdk5"))


assemblyOutputPath in assembly := file("target/distgrader.jar")
