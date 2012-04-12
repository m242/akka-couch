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

object DBLog {

  // A fire-and-forget logging implementation as a singleton. I generally call
  // this like so:
  //
  // DBLog.log(Map("name" -> "Roy Batty", "model" -> "Nexus Six",
  // 		"inceptDate" -> new Long(new Date().getTime)))
  //
  // Ektorp only allows java.util.Map[String, j.u.Object] as scalar entries, and
  // the JavaConversion package doesn't cut it, so I just iterate through the Map
  // and create a new jumap.

  def log(obj:Map[String, AnyRef]) {
    val jumap = new java.util.HashMap[String, AnyRef]()
    obj.foreach { keyVal => jumap.put(keyVal._1, keyVal._2) }
    CouchSystem.couchSupervisor ! ("create", jumap)
  }
}