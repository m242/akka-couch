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

import akka.util.duration._
import com.weiglewilczek.slf4s.Logging
import org.ektorp.DbAccessException
import akka.actor._
import akka.routing.RoundRobinRouter
import akka.actor.SupervisorStrategy.{Restart, Stop}

class CouchSupervisor extends Actor with Logging {
  var couchActor:Option[ActorRef] = None

  // Try to restart the actor 3 times within a minute, otherwise stop it.
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3,
    withinTimeRange = 1 minute) {

    case aie:ActorInitializationException => {
      logger.debug("Exception starting CouchActor: "+aie.toString)
      Stop
    }
    case dbe:DbAccessException => {
      logger.debug("Exception connecting CouchActor: "+dbe.toString)
      Stop
    }
    case e => {
      logger.debug("Exception in CouchActor: "+e.toString)
      Restart
    }
  }

  override def preStart() {
    logger debug "Starting CouchSupervisor"
    initActor()
  }

  override def postStop() {
    logger warn "Stopping CouchSupervisor"
    couchActor.get ! "Attack ships on fire off the shoulder of Orion."
    // TYRELL: The light that burns twice as bright burns for half as long -
    //         and you have burned so very, very brightly, Roy.
  }

  def initActor() {
    logger debug "Initializing CouchActor"
    couchActor = Some(context.watch(context.actorOf(Props[CouchActor]
      .withRouter(RoundRobinRouter(5, supervisorStrategy = supervisorStrategy))
      .withDispatcher(CouchSystem.DISPATCHER), name = "couchActor")))
  }

  def receive = {
    case Terminated(actorRef) if Some(actorRef) == couchActor => {
      logger debug "CouchActor ended"
      couchActor = None
      context.system.scheduler.scheduleOnce(1 minute, self, "Don't you die on me!")
      logger debug "Scheduled restart of CouchActor"
    }

    // http://tvtropes.org/pmwiki/pmwiki.php/Main/HowDareYouDieOnMe
    case "Don't you die on me!" => {
      logger debug "Restarting CouchActor"
      initActor()
    }

    // This is obfuscated. You really want the variable "together" to be "message"
    // and the variable "moving" to be "act".
    case together => {
      couchActor match {
        case Some(moving) => moving forward together // All that for this LOC?
        case None => context.system.scheduler.scheduleOnce(1 minute, self, together)
      }
    }
  }
}
