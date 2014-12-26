///////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2014 Adobe Systems Incorporated. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
///////////////////////////////////////////////////////////////////////////

import AssemblyKeys._
import com.github.bigtoast.sbtthrift.ThriftPlugin

assemblySettings

jarName in assembly := "Spindle.jar"

// Load "provided" libraries with `sbt run`.
run in Compile <<= Defaults.runTask(
  fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)
)

name := "Spindle"

version := "1.0"

scalaVersion := "2.10.4"

fork in run := true

unmanagedResourceDirectories in Compile <<= Seq(
  baseDirectory / "src/main/webapp",
  baseDirectory / "src/main/resources"
).join

libraryDependencies ++= Seq(
  // Spark dependencies.
  // Mark as provided if distributing to clusters.
  // Don't use 'provided' if running the program locally with `sbt run`.
  "org.apache.spark" %% "spark-core" % "1.2.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.2.0" % "provided",
  // "org.slf4j" % "slf4j-simple" % "1.7.7", // Logging.
  "org.json4s" %% "json4s-native" % "3.2.10", // JSON parsing.
  "org.apache.hadoop" % "hadoop-client" % "2.4.0" % "provided" excludeAll(
    ExclusionRule(organization = "org.jboss.netty"),
    ExclusionRule(organization = "io.netty"),
    ExclusionRule(organization = "org.eclipse.jetty"),
    ExclusionRule(organization = "org.mortbay.jetty"),
    ExclusionRule(organization = "org.ow2.asm"),
    ExclusionRule(organization = "asm")
  ),
  "io.spray" % "spray-can" % "1.3.1",
  "io.spray" % "spray-routing" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4" excludeAll(
    ExclusionRule(organization = "org.slf4j")
  ),
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "com.twitter" % "parquet-thrift" % "1.5.0",
  "com.google.guava" % "guava" % "17.0",
  "org.joda" % "joda-convert" % "1.6",
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.1.0",
  "joda-time" % "joda-time" % "2.3"

)

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Spray repo" at "http://repo.spray.io",
  "sonatype" at "https://oss.sonatype.org/content/groups/public",
  "Twitter" at "http://maven.twttr.com/"
)

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

seq(ThriftPlugin.thriftSettings: _*)
mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}
