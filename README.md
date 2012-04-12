# akka-couch - Communicate with CouchDB via Akka

* Fault-tolerance by way of file-based mailboxes
* Returns messages back to the mailbox if CouchDB dies
* Attempts to reconnect to CouchDB automatically and redeliver

## Requirements

* [Ektorp](http://www.ektorp.org/)
* [Akka 2.0](http://akka.io/)
