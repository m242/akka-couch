package net.markbeeson.akkacouch

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 4/9/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */

trait CouchDbDocument extends Serializable {
  def getId():String
  def setId(id:String)
  def getRevision():String
  def setRevision(rev:String)

  def isNew:Boolean
  def hasConflict:Boolean
}