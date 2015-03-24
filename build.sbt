name := "kolektor"

organization := "com.microWorkflow"

version := "1.0-SNAPSHOT"

description := "collectd publisher for Kafka"

publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

libraryDependencies ++= Seq(
  "org.junit" % "junit" % "4.10" % "test"
  , "org.mockito" % "mockito-core" % "1.9.5" % "test"
)
