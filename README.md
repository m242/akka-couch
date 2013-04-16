# akka-couch - Communicate with CouchDB via Akka

* Fault-tolerance by way of file-based mailboxes
* Returns messages back to the mailbox if CouchDB dies
* Attempts to reconnect to CouchDB automatically and redeliver

## Requirements

* [Ektorp](http://www.ektorp.org/)
* [Akka 2.1](http://akka.io/)
* [SLF4S](https://github.com/weiglewilczek/slf4s)
* [jackson scala module](https://github.com/FasterXML/jackson-module-scala)

## Akka-Couch 1.0.x to 1.1.x Migration 
* Akka-Couch 1.1 is build using scala 2.10.1.  You must convert your existing project with scala 2.10 or above to use this library.
* Changes in application.conf, akka-2.1 FileBasedMailboxType have moved to akka.actor.mailbox.filebased.FileBasedMailboxType.  Make sure you made the same change in your conf file.
* Upgrade jackson library to jackson-scala-module 2.0.  If you were using jackson 1.x before, make sure you update all your import statements.  Do a Find & Replace for import org.codehaus.jackson to com.fasterxml.jackson.
