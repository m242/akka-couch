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

trait CouchDB {
  val URL = "http://localhost:5984/"
  val DB = "myCouchDb"

  val db = {
    val httpClient = new StdHttpClient.Builder().url(URL).build()
    new StdCouchDbInstance(httpClient).createConnector(DB, true)
  }

  def get(T:Class[_], id:String):Option[Any] = {
    try {
      Some(db.get(T, id))
    } catch {
      case e => None
    }
  }

  def create(obj:AnyRef) {
    db create obj
  }

  def update(obj:AnyRef) {
    db update obj
  }

  def delete(obj:AnyRef) {
    db delete obj
  }
}