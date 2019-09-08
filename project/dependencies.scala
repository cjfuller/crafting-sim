package deps

import sbt._

object Dependencies {
  // TODO(colin): reorganize the code such that the dependencies can be separate
  // for server and cli?
  val logback        = "ch.qos.logback"             % "logback-classic"      % "1.2.3"
  val json4s         = "org.json4s"                 %% "json4s-native"       % "3.6.7"
  val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.2"
  val clist          = "org.backuity.clist"         %% "clist-core"          % "3.5.1"
  val clistMacros    = "org.backuity.clist"         %% "clist-macros"        % "3.5.1" % "provided"
  val scalatra       = "org.scalatra"               %% "scalatra"            % "2.7.0-RC1"
  val scalatraJson   = "org.scalatra"               %% "scalatra-json"       % "2.7.0-RC1"
  val jetty          = "org.eclipse.jetty"          % "jetty-webapp"         % "9.4.9.v20180320"
  val servlet        = "javax.servlet"              % "javax.servlet-api"    % "3.1.0" % "provided"
  val scalacache     = "com.github.cb372"           %% "scalacache-core"     % "0.28.0"
  val scalacacheImpl = "com.github.cb372"           %% "scalacache-caffeine" % "0.28.0"

  val dependencies = List(
    logback,
    json4s,
    scalaLogging,
    clist,
    clistMacros,
    scalatra,
    scalatraJson,
    jetty,
    servlet,
    scalacache,
    scalacacheImpl
  )
}
