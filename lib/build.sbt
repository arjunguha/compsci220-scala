bintrayOrganization := Some("plasma-umass")
licenses += ("BSD", url("https://opensource.org/licenses/BSD-3-Clause"))

name := "compsci220"
organization := "edu.umass.cs"
version := "1.0.0"
scalaVersion := "2.11.7"

scalacOptions in (Compile,doc) ++=
  Seq("-groups",
      "-implicits")

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

libraryDependencies ++=
  Seq("org.scalatest" %% "scalatest" % "2.2.6" % "test")

  target in Compile in doc := baseDirectory.value / ".." / "website" / "api"
