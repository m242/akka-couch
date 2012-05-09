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
