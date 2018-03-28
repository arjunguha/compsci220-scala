libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4"
test in assembly := {}
mainClass in assembly := Some("GradingMain")
assemblyOutputPath in assembly := new java.io.File("./project.jar")
libraryDependencies += "edu.umass.cs" %% "compsci220" % "1.9.0"

