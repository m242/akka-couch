name := "akka-couch"

organization := "net.markbeeson"

version := "1.1.1"

scalaVersion := "2.10.1"

description := "Connector for Akka to talk to CouchDB"

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.1.3",
	"org.ektorp" % "org.ektorp" % "1.3.0",
	"com.typesafe.akka" % "akka-actor_2.10" % "2.1.2",
	"com.typesafe.akka" % "akka-file-mailbox_2.10" % "2.1.2",
	"com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7",
	"org.specs2" % "specs2_2.10" % "1.14",
	"com.typesafe" % "config" % "1.0.0"
)

publishTo := Some(Resolver.url("ultra", url("http://ultra.skechers.com:8081/artifactory/skechers"))(Patterns(true, "[organization]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]")))
//publishTo :=  Some("Skechers Artifacts" at "http://ultra:8081/artifactory/snapshots")