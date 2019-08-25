import sbtassembly.AssemblyPlugin.defaultUniversalScript

ThisBuild / organization := "org.volume14"
ThisBuild / scalaVersion := "2.13.0"
ThisBuild / version := "0.0.1"

lazy val root = (project in file("."))
  .settings(
    name := "crafting-sim",
    libraryDependencies += "ch.qos.logback"             % "logback-classic" % "1.2.3",
    libraryDependencies += "org.json4s"                 %% "json4s-native"  % "3.6.7",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2",
    libraryDependencies ++= Seq(
      "org.backuity.clist" %% "clist-core"   % "3.5.1",
      "org.backuity.clist" %% "clist-macros" % "3.5.1" % "provided"
    )
  )

assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(prependShellScript = Some(defaultUniversalScript(shebang = true)))

assemblyJarName in assembly := s"${name.value}"
