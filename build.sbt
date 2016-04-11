name := """parallel-apriori"""

version := "1.0"

scalaVersion := "2.10.4"

lazy val akkaVersion = "2.3.15"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.10" % "2.3.11",
  "com.typesafe.akka" % "akka-testkit_2.10" % "2.3.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.apache.spark"  %% "spark-core"    % "1.6.1",
  "org.apache.hadoop"  % "hadoop-client" % "2.4.0"
)


resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

fork in run := true
