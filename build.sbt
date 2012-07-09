sbtPlugin := true

name := "akka-couch"

organization := "net.markbeeson"

version := "1.0.9"

description := "Connector for Akka to talk to CouchDB"

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
	"org.ektorp" % "org.ektorp" % "1.2.2",
	"com.typesafe.akka" % "akka-actor" % "2.0.1",
	"com.typesafe.akka" % "akka-file-mailbox" % "2.0.1",
	"com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7",
	"org.specs2" %% "specs2" % "1.8.2" % "test",
	"com.typesafe" % "config" % "0.4.0"
)
