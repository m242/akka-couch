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

import akka.util.Timeout
import akka.util.duration._
import akka.pattern.ask //Need to keep this for ? operator
import akka.dispatch.Await
import org.ektorp.ViewQuery

/**
 * Created with IntelliJ IDEA.
 * User: randyu
 * Date: 4/30/12
 * Time: 12:12 PM
 */

trait PersistenceIfc {

  def create(obj: AnyRef)

  def read[T <: AnyRef : Manifest](id: String): Option[T]
//  def readNoCache[T <: AnyRef : Manifest](id: String): Option[String]

  def update(obj: AnyRef)

  def delete(obj: AnyRef)

  def query[T <: AnyRef : Manifest](viewQuery: ViewQuery): List[T]
//  def queryNoCache[T <: AnyRef](viewQuery: ViewQuery): List[String]

  def kvQuery[T <: AnyRef : Manifest](viewQuery: ViewQuery): List[(String, T)]

  def createAtomic[T <: AnyRef](obj: T): T
}

trait StringToType extends AkkaCouchClient {  //This solves the ambiguous conversion problem
  def readAsString[T](id:String): Option[String] = super.read(id)
  def queryAsString[T](viewQuery: ViewQuery): List[String] = super.query(viewQuery)
  def kvQueryAsString[T](viewQuery: ViewQuery): List[(String, AnyRef)] = super.kvQuery(viewQuery)
}

trait StringIfc {

  def create(obj: AnyRef)

  def read(id: String): Option[String]
  //  def readNoCache[T <: AnyRef : Manifest](id: String): Option[String]

  def update(obj: AnyRef)

  def delete(obj: AnyRef)

  def query(viewQuery: ViewQuery): List[String]
  //  def queryNoCache[T <: AnyRef](viewQuery: ViewQuery): List[String]

  def kvQuery(viewQuery: ViewQuery): List[(String, AnyRef)]

  def createAtomic[T <: AnyRef](obj: T): T
}

trait AkkaCouchClient extends StringIfc with AkkaCouchSettings{
  //todo: pull these values from elsewhere: config file?

  implicit lazy val dur = 1 milli //5 seconds
  implicit lazy val timeout = Timeout(dur)

  def create(obj: AnyRef) {
    CouchSystem.couchSupervisor ! Create(obj)
  }

  def read(id: String): Option[String] = {
    Await.result(CouchSystem.couchSupervisor ? Read(id), dur).asInstanceOf[Option[String]]
  }

  def update(obj: AnyRef) {
    CouchSystem.couchSupervisor ! Update(obj)
  }

  def delete(obj: AnyRef) {
    CouchSystem.couchSupervisor ! Delete(obj)
  }

//  def query(design: String, view: String, startKey: Option[_] = None, endKey: Option[_] = None): List[String] = {
//    Await.result(CouchSystem.couchSupervisor ? new Query(design, view, startKey, endKey), dur).asInstanceOf[List[String]]
//  }

  def query(viewQuery: ViewQuery): List[String] = {
    viewQuery.dbPath(db.path) //add db path, force early build of query.
    Await.result(CouchSystem.couchSupervisor ? Query(viewQuery, None), dur).asInstanceOf[List[String]]
  }

  def kvQuery(viewQuery: ViewQuery): List[(String, AnyRef)] = {
    viewQuery.dbPath(db.path) //add db path, force early build of query.
    Await.result(CouchSystem.couchSupervisor ? Query(viewQuery, Some(true)), dur).asInstanceOf[List[(String, String)]]
  }

  def createAtomic[T <: AnyRef](obj: T): T = {
    Await.result(CouchSystem.couchSupervisor ? Create(obj), dur).asInstanceOf[T]
  }
}

object AkkaCouchClient extends AkkaCouchClient {
  val d = new akka.util.DurationInt(5000) //5 seconds

  override implicit lazy val dur = d.millis

  override implicit lazy val timeout = Timeout(dur)
}
