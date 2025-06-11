import sbt.Keys.dependencyOverrides
import sbtassembly.AssemblyKeys.assembly

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    name := "spark-batch",

    libraryDependencies ++= Seq(
      // Spark Core + SQL
      "org.apache.spark" %% "spark-core" % "3.5.5" % Provided,
      "org.apache.spark" %% "spark-sql"  % "3.5.5" % Provided,
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.5",

      //AWS
      "org.apache.hadoop" % "hadoop-aws" % "3.3.1",
      "com.amazonaws" % "aws-java-sdk-bundle" % "1.12.520",

      // Logging
      "org.apache.logging.log4j" % "log4j-api"         % "2.20.0",
      "org.apache.logging.log4j" % "log4j-core"        % "2.20.0",
      "org.apache.logging.log4j" % "log4j-slf4j2-impl" % "2.20.0",

      //Postgresql
      "org.postgresql" % "postgresql" % "42.7.3"
    ),

    dependencyOverrides +="org.slf4j" % "slf4j-api" % "2.0.7",

    assembly / mainClass := Some("com.example.currency.BatchProcessor"),
    assembly / assemblyJarName := "spark-batch.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
      case PathList("META-INF", xs @ _*)             => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )

