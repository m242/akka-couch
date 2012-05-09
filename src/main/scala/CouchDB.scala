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
import org.ektorp.impl.StdCouchDbInstance
import java.io.InputStream
import org.ektorp.{UpdateConflictException, DocumentNotFoundException, ViewQuery}
import com.typesafe.config._

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
    new StdCouchDbInstance(httpClient).createConnector(DB, true)
  }

  def create(obj: AnyRef): AnyRef = {
    db create obj
    obj
  }

  def read(id: String): Option[InputStream] = {
    try{
      Option(db getAsStream id)
    } catch {
      case e: DocumentNotFoundException => None
    }
  }

  def update(obj: AnyRef) {
    db update obj
  }

  def delete(obj: AnyRef) {
    try{
//      println("Deleting: "+AppUtils.toJson(obj))
      db delete obj
    } catch {
      case e: UpdateConflictException => {} //probably a 409 error, no _rev field
    }
  }

  def query(design: String, view: String, key: Option[String]): List[String] = {
    import scala.collection.JavaConversions._
    val query = new ViewQuery().designDocId("_design/" + design).viewName(view)
    key.foreach(k => query.key(k))
    val r = db.queryView(query).getRows
    r.map(_.getValue).toList
  }

}