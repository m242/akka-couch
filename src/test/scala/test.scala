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

/**
 * Created with IntelliJ IDEA.
 * User: randyu
 * Date: 5/1/12
 * Time: 2:51 PM
 */

import org.specs2.mutable
import org.specs2.mutable._
import org.ektorp.support.CouchDbDocument
import org.ektorp.impl.StdObjectMapperFactory
import java.io.InputStream

class AkkaCouch extends Specification {

  "AkkaCouchClient" should {
    "exist" in new couchRecord{
      AkkaCouchClient must not be equalTo(null)
    }
  }

  "AkkaCouchClient" should {
    "write, sleep, then read a TestValue" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      override val testRecord = Option(TestValue(testId))
      AkkaCouchClient.create(testRecord.get)
      Thread.sleep(2000)
      val res = AkkaCouchClient.read(testId)
      res must not be equalTo(None)
    }
  }

  "AkkaCouchClient" should {
    "atomically write, then immediately read a TestValue" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      override val testRecord = Option(TestValue(testId))
      AkkaCouchClient.createAtomic(testRecord.get)
      val res = AkkaCouchClient.read(testId)
      res must not be equalTo(None)
    }
  }

  "AkkaCouchClient" should {
    "return None if not found" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      val res = AkkaCouchClient.read(testId)
      res must be equalTo(None)
    }
  }

  trait couchRecord extends mutable.After {
    private[this] def valueOf[T <: AnyRef](in: InputStream, clazz: Class[T]): T = {
      val mapperFactory = new StdObjectMapperFactory()
      val jacksonMapper = mapperFactory.createObjectMapper()
      jacksonMapper.readValue(in, clazz) //manifest[T].erasure) //todo: use FasterXML-jackson and jacks on github to obviate clazz
    }

    val testRecord: Option[CouchDbDocument] = None
    def after() { //clean up created record
      try{
        testRecord.foreach(r => {
          AkkaCouchClient.read(r.getId).foreach(s => {
            AkkaCouchClient.delete(valueOf(s, classOf[TestValue]))
          })
        })
      } catch {
        case e:Exception => {
          println("Error during cleanup")
          e.printStackTrace()
        }
      }
    }
  }
}

case class TestValue(id: String) extends CouchDbDocument {
  def this(){this(null)}
  Option(id).foreach(i => setId(i))
}
