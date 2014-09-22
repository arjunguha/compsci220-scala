name := "joinlists"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

scalacOptions += "-feature"

scalaVersion := "2.11.2"

// The root project sets up this dependency, so we don't need it here.
// But, students will need it.
// libraryDependencies += "edu.umass.cs" %% "cs220" % "1.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
