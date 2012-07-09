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

import org.ektorp.http.StdHttpClient
import com.typesafe.config._
import org.ektorp.support.CouchDbDocument
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.JsonNode
import akka.japi.Option.Some
import org.ektorp.{UpdateConflictException, DocumentNotFoundException, ViewQuery}
import org.ektorp.impl.{BulkOperation, JsonSerializer, StdCouchDbConnector, StdCouchDbInstance}

object Serializer extends JsonSerializer {
  def createBulkOperation(obj: java.util.Collection[_], allO: Boolean): BulkOperation = null: BulkOperation
  def toJson(o: Object) = com.codahale.jerkson.Json.generate(o)
  def apply() = this
}

trait CouchDB {

  lazy val conf = ConfigFactory.load()

  lazy val URL: String = try {
    conf.getString("akka-couch.host")
  } catch {
    case e: ConfigException.Missing => {
      println("Missing setting: akka-couch.host")
      throw e
      ""
    }
  }

  lazy val DB: String = try {
    conf.getString("akka-couch.db")
  } catch {
    case e: ConfigException.Missing => {
      println("Missing setting: akka-couch.db")
      throw e
      ""
    }
  }

  lazy val db = {
    val httpClient = new StdHttpClient.Builder().url(URL).build()
    val conn = new StdCouchDbInstance(httpClient).createConnector(DB, true)
    conn.asInstanceOf[StdCouchDbConnector].setJsonSerializer(Serializer())
    conn
  }

  def create(obj: AnyRef): AnyRef = {
    db create obj
    obj
  }

  def read(id: String): Option[String] = {
    try{
      Some(scala.io.Source.fromInputStream(db getAsStream id).mkString)
    } catch {
      case e: DocumentNotFoundException => None
    }
  }

  def update(obj: AnyRef) {
    def latestRevision(id: String): Option[String] = {
      read(id).map(doc => new ObjectMapper().readValue(doc, classOf[JsonNode]).path("_rev").getValueAsText)
    }

    val doc = obj.asInstanceOf[CouchDbDocument]
    if(Option(doc.getRevision).isEmpty) { //Need a revision field in order to update. If no rev field, need to get one.
      latestRevision(doc.getId).foreach(rev => doc.setRevision(rev))
    }
    db update obj
  }

  def delete(obj: AnyRef) {
    try{
      db delete obj
    } catch {
      case e: UpdateConflictException => {} //probably a 409 error, no _rev field
    }
  }

  def query(design: String, view: String, key: Option[String]): List[String] = {
    import scala.collection.JavaConversions._
    val query = new ViewQuery().designDocId("_design/" + design).viewName(view)
    key.foreach(k => query.key(k))
    db.queryView(query).getRows.map(_.getValue).toList
  }

}