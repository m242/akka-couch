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

import akka.actor.Actor
import com.weiglewilczek.slf4s.Logging

class CouchActor extends Actor with Logging with CouchDB {
  def receive = {
    case "Attack ships on fire off the shoulder of Orion." => {
      logger debug "Received stop message"
      context stop self
    }

    // This implementation uses tuples for messages. They're quick, they're easy
    // to understand, and they're pretty quick to construct in code. Your mileage
    // may vary. The important part is to not intercept exceptions, and let them
    // bubble up to the supervisor.

    case ("get", c:Class[Any], key:String) => {
      logger debug "Received get message"
      sender ! get(c, key)
    }

    case ("create", obj:AnyRef) => {
      logger debug "Received create message"
      create(obj)
    }

    case ("update", obj:AnyRef) => {
      logger debug "Received update message"
      update(obj)
    }

    case ("delete", obj:AnyRef) => {
      logger debug "Received delete message"
      delete(obj)
    }

    // Add any other messages you see fit here. Compaction, multi-get, etc.

    case _ => logger debug "Received unknown message"
  }
}
