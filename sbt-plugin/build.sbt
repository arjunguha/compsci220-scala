
sbtPlugin := true
scalaVersion := "2.12.4"
name := "compsci220"
organization := "edu.umass.cs"
version := "1.1.0"
scalacOptions += "-feature"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.apache.commons" % "commons-compress" % "1.8.1",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "io.spray" %%  "spray-json" % "1.3.3")

publishMavenStyle := false
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
bintrayRepository := "sbt-plugins"
bintrayOrganization in bintray := None

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
