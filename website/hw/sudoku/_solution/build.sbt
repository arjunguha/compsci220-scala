scalaVersion := "2.11.2"

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

fork in Test := true

testGrouping <<= definedTests in Test map(_.groupBy(_.name).map({
  case (name, tests) => new Tests.Group(name, tests, Tests.SubProcess(Seq()))
}).toSeq)
