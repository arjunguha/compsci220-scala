import scala.util.matching.Regex
import scala.util.matching.Regex.Match

bintrayOrganization := Some("plasma-umass")
licenses += ("BSD", url("https://opensource.org/licenses/BSD-3-Clause"))

name := "hof-testing-lab"
organization := "edu.umass.cs"
version := "1.0.0"
scalaVersion := "2.12.4"

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")
