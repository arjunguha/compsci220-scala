scalaVersion := "2.11.7"
scalacOptions ++=
 Seq("-deprecation",
     "-unchecked",
     "-feature",
     "-Xfatal-warnings")
resolvers += "arjun-bintray" at "http://dl.bintray.com/plasma-umass/maven"
libraryDependencies += "edu.umass.cs" %% "pdf" % "1.0"