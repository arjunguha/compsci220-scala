import AssemblyKeys._

lazy val root = project.in(file(".")).aggregate(support, submission)

lazy val support = project.in(file("support-code"))
                          .settings(assemblySettings: _*)
                          .settings(jarName in assembly := "cmpsci220.jar")

lazy val submission = project.settings(assemblySettings: _*)
                             .settings(assemblySettings: _*)
                             .settings(jarName in assembly := "submission.jar")