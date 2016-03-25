scalaVersion := "2.11.2"

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

parallelExecution in Test := false

fork in Test := true