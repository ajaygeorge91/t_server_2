package userauth.models

import com.example.database.graph.schema.VertexLabels
import com.mohiva.play.silhouette.api.util.PasswordInfo
import gremlin.scala.label

@label(VertexLabels.PasswordInfo)
final case class PasswordInfoVertex(
  hasher: String,
  password: String,
  salt: Option[String] = None
)

object PasswordInfoVertex {

  def fromPasswordInfo(passwordInfo: PasswordInfo): PasswordInfoVertex = PasswordInfoVertex(
    passwordInfo.hasher,
    passwordInfo.password,
    passwordInfo.salt
  )

  def toPasswordInfo(passwordInfoVertex: PasswordInfoVertex): PasswordInfo = PasswordInfo(
    passwordInfoVertex.hasher,
    passwordInfoVertex.password,
    passwordInfoVertex.salt
  )

  def update(passwordInfoVertex: PasswordInfoVertex, passwordInfo: PasswordInfo): PasswordInfoVertex = PasswordInfoVertex(
    passwordInfo.hasher,
    passwordInfo.password,
    passwordInfo.salt
  )

}