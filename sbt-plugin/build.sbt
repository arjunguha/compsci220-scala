import bintray.Keys._

sbtPlugin := true

scalaVersion := "2.10.4"

name := "cmpsci220"

organization := "edu.umass.cs"

version := "2.2"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.apache.commons" % "commons-compress" % "1.8.1",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "io.spray" %%  "spray-json" % "1.2.6")

publishMavenStyle := false

bintrayPublishSettings

repository in bintray := "sbt-plugins"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayOrganization in bintray := None
