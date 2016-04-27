import sbt.Keys._

organization := "com.measurence"

name := "scala-italy-2016-akka-stream"

description := "example code from akka stream talk @ scala taly 2016"

scalaVersion := "2.11.7"

val akkaVersion = "2.4.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka"        %% "akka-stream"             % akkaVersion,
  "com.typesafe.akka"        %% "akka-http-core"          % akkaVersion,
  "com.typesafe.akka"        %% "akka-http-experimental"  % akkaVersion,
  "com.typesafe.akka"        %% "akka-actor"              % akkaVersion,
  "com.typesafe.akka"        %% "akka-slf4j"              % akkaVersion,
  "io.spray"                 %% "spray-json"              % "1.3.2",
  "com.typesafe.akka"        %% "akka-testkit"            % akkaVersion        % "test",
  "com.typesafe.akka"        %% "akka-stream-testkit"     % akkaVersion        % "test",
  "com.typesafe.akka"        %% "akka-http-testkit"       % akkaVersion        % "test"
)