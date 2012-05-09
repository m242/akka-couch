/*
 * Copyright 2012 Mark Beeson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.markbeeson.akkacouch

import akka.actor.{Props, ActorSystem}

object CouchSystem {
  val DISPATCHER = "mydispatcher"
  val system = ActorSystem("MySystem")

  val couchSupervisor = CouchSystem.system.actorOf(Props[CouchSupervisor]
    .withDispatcher(DISPATCHER), "couchSupervisor")
}

case class Create(obj: AnyRef)
case class Read(id: String)
case class Update(obj: AnyRef)
case class Delete(obj: AnyRef)
case class Query(design: String, view: String, key: Option[String])

