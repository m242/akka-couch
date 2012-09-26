name := "akka-couch"

organization := "net.markbeeson"

version := "1.0.15"

description := "Connector for Akka to talk to CouchDB"

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
	, "repo.codahale.com" at "http://repo.codahale.com"
)

libraryDependencies ++= Seq(
	"com.codahale" % "jerkson_2.9.1" % "0.5.0",
	"org.ektorp" % "org.ektorp" % "1.2.2" withSources(),
	"com.typesafe.akka" % "akka-actor" % "2.0.1",
	"com.typesafe.akka" % "akka-file-mailbox" % "2.0.1",
	"com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7",
	"org.specs2" %% "specs2" % "1.12.1" % "test",
	"com.typesafe" % "config" % "0.4.0"
)

publishTo := Some(Resolver.url("ultra", url("http://ultra.skechers.com:8081/artifactory/skechers"))(Patterns(false, "[organization]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]")))