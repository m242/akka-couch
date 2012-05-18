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
import akka.pattern.ask
import akka.dispatch.Await
import java.io.InputStream

/**
 * Created with IntelliJ IDEA.
 * User: randyu
 * Date: 4/30/12
 * Time: 12:12 PM
 */

trait AkkaCouchClient {
  //todo: pull these values from elsewhere: config file?

  implicit lazy val dur = 5 seconds
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

  def query(design: String, view: String, key: Option[String] = None): List[String] = {
    Await.result(CouchSystem.couchSupervisor ? Query(design, view, key), dur).asInstanceOf[List[String]]
  }

  def createAtomic[T <: AnyRef](obj: T): T = {
    Await.result(CouchSystem.couchSupervisor ? Create(obj), dur).asInstanceOf[T]
  }
}

object AkkaCouchClient extends AkkaCouchClient
