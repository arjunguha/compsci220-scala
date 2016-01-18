// NOTE: The controller project has transitive dependency on Google Guava, which includes a duplicate copy of
// an Apache library, so we can't create a fat JAR that includes the controller. This is the main reason why
// we have sub-project for the worker.

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

lazy val shared = project
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.1",
      "com.typesafe.akka" %% "akka-remote" % "2.4.1",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.1",
      "org.slf4j" % "slf4j-simple" % "1.7.12"
    ))

lazy val worker = project
  .dependsOn(shared)
  .settings(
    libraryDependencies ++= Seq(
      "com.ning" % "async-http-client" % "1.9.31",
      "com.lihaoyi" %% "upickle" % "0.3.6",
      "com.spotify" % "docker-client" % "3.5.9",
      "commons-io" % "commons-io" % "2.4",
      "edu.umass.cs" %% "zip" % "1.2",
//      ("com.github.docker-java" % "docker-java" % "2.1.2")
//        .exclude("org.slf4j", "jcl-over-slf4j"),
      "org.scalatest" %% "scalatest" % "2.2.1" % "test"
    ),
    assemblyOutputPath in assembly := file("target/worker.jar")
  )

lazy val controller = project
  .dependsOn(shared)
  .settings(
     libraryDependencies ++= Seq(
       "com.google.api-client" % "google-api-client" % "1.21.0",
       "com.google.apis" % "google-api-services-compute" % "v1-rev88-1.21.0",
       "com.google.apis" % "google-api-services-storage" % "v1-rev57-1.21.0",
       "com.spotify" % "docker-client" % "3.5.9",
       "org.scalatest" %% "scalatest" % "2.2.1" % "test",
       "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
       "commons-codec" % "commons-codec" % "1.10",
       "com.lihaoyi" %% "upickle" % "0.3.6"
     )
  )


lazy val root = project.in(file(".")).aggregate(worker, controller)


compile <<= (compile in Compile)
  .dependsOn(assembly in Compile in worker, compile in Compile in controller)
