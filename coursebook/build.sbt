scalaVersion := "2.11.8"
scalacOptions ++=
 Seq("-deprecation",
     "-unchecked",
     "-feature",
     "-Xfatal-warnings")
resolvers += "PLASMA" at "http://dl.bintray.com/plasma-umass/maven"
libraryDependencies += "edu.umass.cs" %% "pdf" % "1.0"