package net.markbeeson.akkacouch.serializer.jackson

import net.markbeeson.akkacouch.JsonSerializer

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 4/9/13
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */

object JacksonSerializer {
  def apply() = new JacksonSerializer
}

class JacksonSerializer extends JsonSerializer {
  def toJson(o: Object) = JacksonWrapper.serialize(o)
  def parseJson[T: Manifest](json: String) = JacksonWrapper.deserialize[T](json)
}