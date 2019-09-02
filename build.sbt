import sbtassembly.AssemblyPlugin.defaultUniversalScript
import _root_.target._
import deps.Dependencies
import scala.sys.process._

ThisBuild / organization := "org.volume14"
ThisBuild / scalaVersion := "2.13.0"
ThisBuild / version := "0.0.1"

lazy val assembleUnix =
  taskKey[File]("assemble the executable jar for unix systems")
lazy val assembleWindows =
  taskKey[File]("assemble the executable jar for windows systems")
lazy val assembleServer =
  taskKey[File]("assemble a standalone jar for the webapp")
lazy val server   = taskKey[Unit]("run the webserver")
lazy val elmBuild = taskKey[Unit]("build the frontend js")
lazy val elmBuildOptimized =
  taskKey[Unit]("build the optimized prod frontend js")

lazy val root = (project in file("."))
  .settings(
    name := "crafting-sim",
    libraryDependencies ++= Dependencies.dependencies,
    assembleUnix := (Def.taskDyn {
      GlobalTarget.assembleTarget = Unix
      GlobalTarget.mainClass = Some("craftingsim.Main")
      assembly
    }).value,
    assembleWindows := (Def.taskDyn {
      GlobalTarget.assembleTarget = Windows
      GlobalTarget.mainClass = Some("craftingsim.Main")
      assembly
    }).value,
    assembleServer := (Def.taskDyn {
      elmBuildOptimized.value
      GlobalTarget.assembleTarget = Server
      GlobalTarget.mainClass = Some("server.ServerMain")
      assembly
    }).value,
    server := (Def.taskDyn {
      GlobalTarget.assembleTarget = Server
      GlobalTarget.mainClass = Some("server.ServerMain")
      (runMain in Compile).toTask(" " + GlobalTarget.mainClass.get)
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
        case Server =>
          (assemblyOption in assembly).value
      }
    },
    mainClass in assembly := {
      GlobalTarget.mainClass
    },
    mainClass in (Compile, run) := {
      GlobalTarget.mainClass
    },
    assemblyJarName in assembly := {
      GlobalTarget.assembleTarget match {
        case Unix    => s"${name.value}"
        case Windows => s"${name.value}.bat"
        case Server  => s"${name.value}-server.jar"
      }
    },
    elmBuild := {
      val proc = Process(
        Seq(
          "elm",
          "make",
          "src/Main.elm",
          "--output",
          "../src/main/resources/elm.js"
        ),
        cwd = Some(new java.io.File("./frontend"))
      )
      val retval = (proc !)
      if (retval != 0) {
        throw new Exception(s"Elm build exited with status $retval")
      }
    },
    elmBuildOptimized := {
      val proc = Process(
        Seq(
          "elm",
          "make",
          "src/Main.elm",
          "--optimize",
          "--output",
          "../src/main/resources/elm.js"
        ),
        cwd = Some(new java.io.File("./frontend"))
      )
      val retval = (proc !)
      if (retval != 0) {
        throw new Exception(s"Elm build exited with status $retval")
      }
      val minify1 = Process(
        Seq(
          "uglifyjs",
          "src/main/resources/elm.js",
          "--compress",
          "pure_funcs=[F2,F3,F4,F5,F6,F7,F8,F9,A2,A3,A4,A5,A6,A7,A8,A9],pure_getters,keep_fargs=false,unsafe_comps,unsafe"
        )
      )
      val minify2 = Process(
        Seq("uglifyjs", "--mangle", "--output=src/main/resources/elm.js")
      )
      val miniRetval = (minify1 #| minify2 !)
      if (miniRetval != 0) {
        throw new Exception(s"Minification exited with status $miniRetval")
      }
    },
    watchSources := watchSources.value.map { source =>
      new Watched.WatchSource(
        source.base,
        source.includeFilter,
        source.excludeFilter || (f => f.getName() contains "elm.js"),
        source.recursive
      )
    },
    // TODO(colin): add this only for the elm task?
    watchSources += Watched.WatchSource(
      baseDirectory.value / "frontend" / "src",
      (f) => f.getName() endsWith ".elm",
      (f) => false
    )
  )
