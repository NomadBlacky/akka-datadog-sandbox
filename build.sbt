ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "dev.nomadblacky"
ThisBuild / organizationName := "NomadBlacky"

val AkkaVersion  = "2.6.19"
val KamonVersion = "2.5.0"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "akka-datadog-sandbox",
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.4",
      "com.typesafe.akka"  %% "akka-stream"              % AkkaVersion,
      "io.kamon"           %% "kamon-bundle"             % KamonVersion,
      "io.kamon"           %% "kamon-datadog"            % KamonVersion,
      "org.scalatest"      %% "scalatest"                % "3.2.11" % Test
    ),
    fork := true,
    javaOptions ++= Seq(
      "-XX:StartFlightRecording=dumponexit=true"
    ),
    dockerBaseImage := "openjdk:11"
  )
