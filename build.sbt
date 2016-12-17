name := "BraczSearch"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  Seq(
    "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
    "com.typesafe" % "config" % "1.3.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % "test",

    // to avoid warnings about different versions
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "org.scala-lang" % "scala-reflect" % "2.12.1"
  )
}
