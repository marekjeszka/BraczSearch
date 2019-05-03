name := "BraczSearch"

version := "1.4"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "catalog"
  )

coverageExcludedFiles := ".*Routes.*"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    filters,
    "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "org.mockito" % "mockito-all" % "1.8.4" % Test
  )
}

TwirlKeys.templateImports += "catalog._"

herokuAppName in Compile := "powerful-depths-64600"
