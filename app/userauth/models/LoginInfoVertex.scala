package userauth.models

import com.example.database.graph.schema.RelationTypes.PropertyKeys
import com.example.database.graph.schema.VertexLabels
import com.mohiva.play.silhouette.api.LoginInfo
import gremlin.scala.{ Key, label }

@label(VertexLabels.LoginInfo)
final case class LoginInfoVertex(
  providerID: String,
  providerKey: String)

object LoginInfoVertex {

  val providerID: Key[String] = Key(PropertyKeys.ProviderID)
  val providerKey: Key[String] = Key(PropertyKeys.ProviderKey)

  def fromLoginInfo(loginInfo: LoginInfo): LoginInfoVertex = LoginInfoVertex(loginInfo.providerID, loginInfo.providerKey)
  def toLoginInfo(loginInfoVertex: LoginInfoVertex): LoginInfo = LoginInfo(loginInfoVertex.providerID, loginInfoVertex.providerKey)
}
