name := "BraczSearch"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

coverageExcludedFiles := ".*Routes.*"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    filters,
    "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "org.mockito" % "mockito-all" % "1.8.4" % Test
  )
}

TwirlKeys.templateImports += "catalog._"
