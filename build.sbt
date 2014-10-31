name := """HelloPlay"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.akka" %% "akka-remote" % "2.3.3",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.3" % "test",
  "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.3.3" % "test"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"