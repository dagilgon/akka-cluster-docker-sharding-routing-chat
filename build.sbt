name := "akka-docker"
version := "2.5.3"
scalaVersion := "2.11.5"

enablePlugins(JavaAppPackaging)

maintainer := "David Gilsanz Gonzalez"
packageSummary := s"Akka ${version.value} Server"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % version.value,
  "com.typesafe.akka" %% "akka-cluster" % version.value,
  "com.typesafe.akka" %% "akka-cluster-tools" % version.value,
  "com.typesafe.akka" %% "akka-cluster-sharding" % version.value,
  "com.typesafe.akka" %% "akka-http" % "10.0.9", 
  "com.github.scopt" %% "scopt" % "3.2.0"
)

// Create custom run tasks to start a seed and a cluster node
// http://www.scala-sbt.org/0.13.0/docs/faq.html#how-can-i-create-a-custom-run-task-in-addition-to-run
lazy val runSeed = taskKey[Unit]("Start the seed node on 127.0.0.1:2551")
fullRunTask(runSeed, Compile, "es.dgg.MainRouting", "--seed")
fork in runSeed := true

javaOptions in runSeed ++= Seq(
    "-Dclustering.ip=127.0.0.1",
    "-Dclustering.port=2551"
)

lazy val runNode = taskKey[Unit]("Start a node on 127.0.0.1:2552")
fullRunTask(runNode, Compile, "es.dgg.MainRouting", "127.0.0.1:2551")
fork in runNode := true

javaOptions in runNode ++= Seq(
    "-Dclustering.ip=127.0.0.1",
    "-Dclustering.port=2552"
)


lazy val runNode2 = taskKey[Unit]("Start a node on 127.0.0.1:2553")
fullRunTask(runNode2, Compile, "es.dgg.MainRouting", "127.0.0.1:2551")
fork in runNode2 := true

javaOptions in runNode2 ++= Seq(
    "-Dclustering.ip=127.0.0.1",
    "-Dclustering.port=2553"
)

