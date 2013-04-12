package net.markbeeson.akkacouch.serializer.jackson

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty, JsonInclude}
import com.fasterxml.jackson.annotation.JsonInclude.Include
import org.ektorp.Attachment
import org.ektorp.support.Revisions

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 4/11/13
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(Include.NON_NULL)
class CouchDbDocument extends net.markbeeson.akkacouch.CouchDbDocument {
  @JsonProperty("_id") var id: String = _
  @JsonProperty("_rev") var rev: String = _
  @JsonProperty("_attachments") var attachments = Map[String, Attachment]()
  @JsonProperty("_conflicts") var conflicts: List[String] = Nil
  @JsonProperty("_revisions") var revisions: Revisions = _

  @JsonIgnore def isNew = rev == null
  @JsonIgnore def hasConflict = !conflicts.isEmpty

  @JsonIgnore def getId() = id
  def setId(id:String) = this.id = id
  @JsonIgnore def getRevision() = rev
  def setRevision(rev:String) = this.rev = rev
}
