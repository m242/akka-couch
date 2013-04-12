package net.markbeeson.akkacouch

import org.ektorp.impl.BulkOperation


/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 4/11/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
trait JsonSerializer extends org.ektorp.impl.JsonSerializer {

  def createBulkOperation(obj: java.util.Collection[_], allO: Boolean): BulkOperation = null: BulkOperation
  def parseJson[T: Manifest](json: String)
}
