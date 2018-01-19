import scala.util.matching.Regex
import scala.util.matching.Regex.Match

bintrayOrganization := Some("plasma-umass")
licenses += ("BSD", url("https://opensource.org/licenses/BSD-3-Clause"))

name := "compsci220"
organization := "edu.umass.cs"
version := "1.4.0"
scalaVersion := "2.12.4"
autoAPIMappings := true

scalacOptions in (Compile,doc) ++=
  Seq("-groups",
      "-implicits")

scalacOptions ++=
  Seq("-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6"

target in Compile in doc := baseDirectory.value / ".." / "website" / "api"

val rtJar: String = System.getProperty("sun.boot.class.path").split(java.io.File.pathSeparator).collectFirst {
  case str: String if str.endsWith(java.io.File.separator + "rt.jar") => str
}.get // fail hard if not found

val javaApiUrl: String = "http://docs.oracle.com/javase/8/docs/api/index.html"

apiMappings += {
  // Lookup the path to jar from the classpath
  val classpath = (fullClasspath in Compile).value
  def findJar(nameBeginsWith: String): File = {
    classpath.find { attributed: Attributed[File] => (attributed.data ** s"$nameBeginsWith*.jar").get.nonEmpty }.get.data // fail hard if not found
  }
  (file(rtJar) -> url(javaApiUrl))
}

lazy val fixJavaLinksTask = taskKey[Unit](
    "Fix Java links - replace #java.io.File with ?java/io/File.html"
)

fixJavaLinksTask := {
  val t = (target in (Compile, doc)).value
  (t ** "*.html").get.filter(hasJavadocApiLink).foreach { f =>
    val newContent = javadocApiLink.replaceAllIn(IO.read(f), fixJavaLinks)
    IO.write(f, newContent)
  }
}

val fixJavaLinks: Match => String = m =>
    m.group(1) + "?" + m.group(2).replace(".", "/") + ".html"

val javadocApiLink = """\"(http://docs\.oracle\.com/javase/8/docs/api/index\.html)#([^"]*)\"""".r

def hasJavadocApiLink(f: File): Boolean = (javadocApiLink findFirstIn IO.read(f)).nonEmpty

fixJavaLinksTask := { fixJavaLinksTask triggeredBy (doc in Compile) }
