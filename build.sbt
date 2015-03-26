name := "kolektor"

organization := "com.microWorkflow"

version := "1.0-SNAPSHOT"

description := "collectd publisher for Kafka"

publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

libraryDependencies ++= {
  val jacksonVersion = "2.1.1"
  Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
    , "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
    , "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    , "com.github.sgroschupf" % "zkclient" % "0.1"
    ,  "junit" % "junit" % "4.10" % "test"
    , "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )
}
