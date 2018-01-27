// Project name (artifact name in Maven)
name := "exec-action"

// orgnization name (e.g., the package name of the project)
organization := "foobar"

version := "1.0-SNAPSHOT"

// project description
description := "Execute tests for 220"

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

// library dependencies. (orginization name) % (project name) % (version)
libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.8.2",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.269"
)