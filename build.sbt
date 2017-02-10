name := "BraczSearch"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
    "com.typesafe" % "config" % "1.3.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % "test"
  )
}
