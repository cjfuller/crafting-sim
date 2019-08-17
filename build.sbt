ThisBuild / organization := "org.volume14"
ThisBuild / scalaVersion := "2.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "crafting-sim",
    libraryDependencies += "ch.qos.logback"             % "logback-classic" % "1.2.3",
    libraryDependencies += "org.json4s"                 %% "json4s-native"  % "3.6.7",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2"
  )
