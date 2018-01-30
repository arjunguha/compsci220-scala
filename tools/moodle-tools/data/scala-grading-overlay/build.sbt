libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4"
test in assembly := {}
mainClass in assembly := Some("GradingMain")
assemblyJarName in assembly := "submission.jar"

