libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4"
test in assembly := {}
mainClass in assembly := Some("GradingMain")
assemblyOutputPath in assembly := new java.io.File("./project.jar")

