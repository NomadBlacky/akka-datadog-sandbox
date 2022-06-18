ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "dev.nomadblacky"
ThisBuild / organizationName := "NomadBlacky"

val AkkaVersion  = "2.6.18"
val KamonVersion = "2.5.0"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin, JavaAgent)
  .settings(
    name := "akka-datadog-sandbox",
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.4",
      "com.typesafe.akka"  %% "akka-stream"              % AkkaVersion,
      "io.kamon"           %% "kamon-bundle"             % KamonVersion,
      "io.kamon"           %% "kamon-datadog"            % KamonVersion,
      "org.scalatest"      %% "scalatest"                % "3.2.12" % Test,
      "ch.qos.logback"      % "logback-classic"          % "1.2.11"
    ),
    run / fork := true,
//    javaAgents ++= Seq(
//      "com.datadoghq" % "dd-java-agent" % "0.102.0" % "runtime"
//    ),
    javaOptions ++= Seq(
      "-XX:StartFlightRecording=dumponexit=true",
      "-XX:FlightRecorderOptions=stackdepth=256"
//      "-Ddd.logs.injection=true",
//      "-Ddd.trace.sample.rate=1",
//      "-Ddd.profiling.enabled=true"
    ),
    dockerBaseImage := "openjdk:11"
  )
