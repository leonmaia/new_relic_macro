import java.io.File

import spray.revolver.RevolverPlugin._

name := """new_relic_macro"""

fork in run := true

resolvers ++= Seq("Twitter Repo" at "http://maven.twttr.com/")

resolvers ++= Seq("Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")

resolvers += "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven"

scalaVersion := "2.11.7"

val finagleVersion = "6.26.0"

organization := "com.lm"

libraryDependencies ++= Seq(
  "com.twitter" %% "twitter-server" % "1.11.0",
  "com.twitter" % "finagle-core_2.11" % finagleVersion,
  "com.twitter" %% "finagle-http" % finagleVersion,
  "com.twitter" %% "finagle-mysql" % finagleVersion,
  ("com.twitter" %% "finagle-stats" % finagleVersion).exclude("asm", "asm"),
  "com.twitter" %% "finagle-zipkin" % finagleVersion,
  "org.slf4j" % "slf4j-log4j12" % "1.7.10",
  "com.newrelic.agent.java" % "newrelic-api" % "3.22.0"
)


assemblyJarName in assembly := s"${name.value}.jar"

parallelExecution in Test := true

addCommandAlias("devrun", "~re-start")

addCommandAlias("cov", "; clean; coverage; test")

Revolver.settings

test in assembly := {}

assemblyMergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case "META-INF/spring.tooling" => MergeStrategy.first
  case x => old(x)
}
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

releaseSettings

publishTo := Some(Resolver.file("Local repo", Path.userHome / ".m2" / "repository" asFile ))

