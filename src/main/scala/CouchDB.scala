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
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import akka.japi.Option.Some
import org.ektorp.{UpdateConflictException, DocumentNotFoundException}
import org.ektorp.impl.{StdCouchDbConnector, StdCouchDbInstance}
import com.weiglewilczek.slf4s.Logger

trait AkkaCouchSettings {
  lazy val conf = ConfigFactory.load()
  lazy val log = Logger("net.markbeeson.akkacouch.AkkaCouchSettings")

  lazy val URL: String = try {
    conf.getString("akka-couch.host")
  } catch {
    case e: ConfigException.Missing => {
      log.error("Missing setting: akka-couch.host")
      throw e
      ""
    }
  }

  lazy val databaseName: String = try {
    conf.getString("akka-couch.db")
  } catch {
    case e: ConfigException.Missing => {
      log.error("Missing setting: akka-couch.db")
      throw e
      ""
    }
  }

//  http://www.ektorp.org/reference_documentation.html#d100e128
  lazy val connectionTimeout: Option[Long] = try {Option(conf.getMilliseconds("akka-couch.connection.connectionTimeout"))} catch {case e: ConfigException.Missing => {None}}
  lazy val socketTimeout: Option[Long] = try {Option(conf.getMilliseconds("akka-couch.connection.socketTimeout"))} catch {case e: ConfigException.Missing => {None}}
  lazy val maxObjectSizeBytes: Option[Int] = try {Option(conf.getInt("akka-couch.connection.maxObjectSizeBytes"))} catch {case e: ConfigException.Missing => {None}}

  lazy val maxConnections: Option[Int] = try {Option(conf.getInt("akka-couch.connection.maxConnections"))} catch {case e: ConfigException.Missing => {None}}
  lazy val cleanupIdleConnections: Option[Boolean] = try {Option(conf.getBoolean("akka-couch.connection.cleanupIdleConnections"))} catch {case e: ConfigException.Missing => {None}}

  lazy val serializerClass: String = try {conf.getString("akka-couch.serializer")} catch {case e: ConfigException.Missing => {"net.markbeeson.akkacouch.serializer.jackson.JacksonSerializer"}}
  lazy val jsonSerializer: JsonSerializer = Class.forName(serializerClass).newInstance().asInstanceOf[JsonSerializer]

  lazy val db = {
    val httpBuilder = new StdHttpClient.Builder().url(URL)
    connectionTimeout.map(t =>  {
      httpBuilder.connectionTimeout(t.toInt)
      log.info("connectionTimeout: " + t)
    })
    socketTimeout.map(t =>  {
      httpBuilder.socketTimeout(t.toInt)
      log.info("socketTimeout: " + t)
    })
    maxObjectSizeBytes.map(s => {
      httpBuilder.maxObjectSizeBytes(s)
      log.info("maxObjectSizeBytes: " + s)
    })
    maxConnections.map(c =>  {
      httpBuilder.maxConnections(c)
      log.info("maxConnections " + c)
    })
    cleanupIdleConnections.map(b =>  {
      httpBuilder.cleanupIdleConnections(b)
      log.info("cleanupIdleConnections: " + b)
    })

    val httpClient = httpBuilder.build()
    val conn = new StdCouchDbInstance(httpClient).createConnector(databaseName, true)
    conn.asInstanceOf[StdCouchDbConnector].setJsonSerializer(jsonSerializer)
    conn
  }

}

trait CouchDB extends AkkaCouchSettings{

  def create[T<:AnyRef](obj: T): T = { // Return T instead of AnyRef
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

  def update[T <: CouchDbDocument](obj: T) {
    def latestRevision(id: String): Option[String] = {                                      //.toString gives a bad value - bad " chars
      read(id).map(doc => new ObjectMapper().readValue(doc, classOf[JsonNode]).path("_rev").textValue)
    }

    if(Option(obj.getRevision).isEmpty) { //Need a revision field in order to update. If no rev field, need to get one.
      latestRevision(obj.getId).foreach(rev => obj.setRevision(rev))
    }
    db update obj
  }

  def delete[T <: CouchDbDocument](obj: T) {
    try{
      db delete obj
    } catch {
      case e: UpdateConflictException => {} //probably a 409 error, no _rev field
    }
  }

  def query(query: Query) = {
    import scala.collection.JavaConversions._
    val viewQuery = SkechersViewQuery(query) //put this query string into the cache of an ektorp ViewQuery
    db.queryView(viewQuery).getRows.map(_.getValue).toList
  }

}
