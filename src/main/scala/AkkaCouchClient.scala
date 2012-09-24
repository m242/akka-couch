package net.markbeeson.akkacouch

import akka.util.Timeout
import akka.util.duration._
import akka.pattern.ask
import akka.dispatch.Await

/**
 * Created with IntelliJ IDEA.
 * User: randyu
 * Date: 4/30/12
 * Time: 12:12 PM
 */

trait AkkaCouchClient {
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

  def query(viewQuery: VQuery): List[String] = {       //can't serialize ViewQuery
    Await.result(CouchSystem.couchSupervisor ? new Query(viewQuery), dur).asInstanceOf[List[String]]
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
