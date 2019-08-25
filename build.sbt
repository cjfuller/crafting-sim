import sbtassembly.AssemblyPlugin.defaultUniversalScript
import _root_.target._

ThisBuild / organization := "org.volume14"
ThisBuild / scalaVersion := "2.13.0"
ThisBuild / version := "0.0.1"

lazy val assembleUnix =
  taskKey[File]("assemble the executable jar for unix systems")
lazy val assembleWindows =
  taskKey[File]("assemble the executable jar for windows systems")

lazy val root = (project in file("."))
  .settings(
    name := "crafting-sim",
    libraryDependencies += "ch.qos.logback"             % "logback-classic" % "1.2.3",
    libraryDependencies += "org.json4s"                 %% "json4s-native"  % "3.6.7",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2",
    libraryDependencies ++= Seq(
      "org.backuity.clist" %% "clist-core"   % "3.5.1",
      "org.backuity.clist" %% "clist-macros" % "3.5.1" % "provided"
    ),
    assembleUnix := (Def.taskDyn {
      GlobalTarget.assembleTarget = Unix
      assembly
    }).value,
    assembleWindows := (Def.taskDyn {
      GlobalTarget.assembleTarget = Windows
      assembly
    }).value,
    assemblyOption in assembly := {
      GlobalTarget.assembleTarget match {
        case Unix => {
          (assemblyOption in assembly).value
            .copy(
              prependShellScript = Some(defaultUniversalScript(shebang = true))
            )
        }
        case Windows => {
          (assemblyOption in assembly).value
            .copy(
              prependShellScript = Some(defaultUniversalScript(shebang = false))
            )
        }
      }
    },
    assemblyJarName in assembly := {
      GlobalTarget.assembleTarget match {
        case Unix    => s"${name.value}"
        case Windows => s"${name.value}.bat"
      }
    }
  )
