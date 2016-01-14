scalaVersion := "2.11.7"
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)