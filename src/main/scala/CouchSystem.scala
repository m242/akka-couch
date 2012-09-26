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
import org.ektorp.ViewQuery

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
//case class Query(design: String, view: String, startKey: Option[_]=None, endKey: Option[_]=None)
case class Query(compiledQuery: String, keys:Option[String])

object Query {
  def apply(viewQuery: ViewQuery):Query = Query(viewQuery.buildQuery, jsonKeysOption(viewQuery)) //compile the serializable query from view query

  def jsonKeysOption(viewQuery: ViewQuery) = if(viewQuery.hasMultipleKeys) Some(viewQuery.getKeysAsJson) else None
}

object SkechersViewQuery {
  def apply(query: Query): SkechersViewQuery = SkechersViewQuery(query.compiledQuery, query.keys)
}

case class SkechersViewQuery(queryString: String, jsonKeysOption:Option[String]) extends ViewQuery {
  override def buildQuery = queryString

  override def hasMultipleKeys = jsonKeysOption.isDefined

  override def getKeysAsJson = jsonKeysOption.getOrElse(super.getKeysAsJson)

}