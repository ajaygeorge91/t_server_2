package userauth.models

import com.example.database.graph.schema.VertexLabels
import com.mohiva.play.silhouette.api.LoginInfo
import gremlin.scala.{id, label}
import org.joda.time.DateTime

@label(VertexLabels.User)
final case class UserVertex(
                             @id id: Long,
                             name: Option[String],
                             email: Option[String],
                             avatarURL: Option[String],
                             activated: Boolean,
                             createdAt:String,
                             updatedAt:String)

object UserVertex {

  def create(user: CreateUser):UserVertex =
    UserVertex(
      id = 0L,
      name = user.name,
      email = user.email,
      avatarURL = user.avatarURL,
      activated = false,
      createdAt = DateTime.now().toString,
      updatedAt = DateTime.now().toString
    )

  def update(userVertex: UserVertex, user: User):UserVertex =
    UserVertex(
      id = user.id,
      name = user.name,
      email = user.email,
      avatarURL = user.avatarURL,
      activated = user.activated,
      createdAt = userVertex.createdAt,
      updatedAt = DateTime.now().toString
    )

  def toUser(userVertex: UserVertex,loginInfo: LoginInfo):User =
    User(
      id = userVertex.id,
      loginInfo = loginInfo,
      name = userVertex.name,
      email = userVertex.email,
      avatarURL = userVertex.avatarURL,
      activated = userVertex.activated,
    )

}