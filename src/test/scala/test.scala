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
import org.codehaus.jackson.annotate.JsonProperty

class AkkaCouch extends Specification {

  "AkkaCouchClient" should {

    "exist" in new couchRecord{
      AkkaCouchClient must not be equalTo(null)
    }

    "write, sleep, then read a TestValue" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      override val testRecord = Option(new TestValue(testId))
      AkkaCouchClient.create(testRecord.get)
      Thread.sleep(2000)
      val res = AkkaCouchClient.read(testId)
      res must not be equalTo(None)
    }

    "atomically write, then immediately read a TestValue" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      override val testRecord = Option(new TestValue(testId))
      AkkaCouchClient.createAtomic(testRecord.get)
      val res = AkkaCouchClient.read(testId)
      res must not be equalTo(None)
    }

    "return None if id to read is not found" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      val res = AkkaCouchClient.read(testId)
      res must be equalTo(None)
    }

//    "write, then respond to Query" in new couchRecord {      //How to test this? -Cannot test because cannot ensure design doc is present
//      val testId = "TEST" + scala.util.Random.nextLong
//      override val testRecord = Option(new TestValue(testId))
//      AkkaCouchClient.createAtomic(testRecord.get)
//      val res = AkkaCouchClient.query("voucher", "by_company", Option(testId))
//
////      res must haveClass List
//    }

    "Atomically create, then update, sleep, then read updated value" in new couchRecord {
      val testId = "TEST" + scala.util.Random.nextLong
      val updated = "Updated123"
      override val testRecord = Option(new TestValue(testId))

      AkkaCouchClient.createAtomic(testRecord.get)
      testRecord.get.value = updated
      AkkaCouchClient.update(testRecord.get)
      Thread.sleep(2000)
      val res = AkkaCouchClient.read(testRecord.get.getId)

      res must not be equalTo(None)
      res.get must contain(updated)
    }

//    "Save new style" in {
//      val i = scala.util.Random.alphanumeric.take(10).mkString
//      println("IIII" + i)
//      val n = Style(i,"name",Option("w"),"some dividsion")
//      println("BBB" + Serializer.toJson(n))
//      val r = AkkaCouchClient.createAtomic(n)
//      println(r)
//      1 must be equalTo(1)
//    }


//    "startkey endkey query" in {
//      val x = AkkaCouchClient.query("order","orderAttempt",Some(1284051684000L),Some(1284059121000L))
//      println(x)
//      1 must be equalTo (1)
//    }

  }


  trait couchRecord extends mutable.After {

    private[this] def valueOf[T <: AnyRef](in: String, clazz: Class[T]): T = {
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

class TestValue(id: String) extends CouchDbDocument {
  @JsonProperty var value:String = "myValue"

  def this(){this(null)}
  Option(id).foreach(i => setId(i))
}
