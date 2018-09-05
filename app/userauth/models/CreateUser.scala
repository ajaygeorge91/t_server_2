package userauth.models

import play.api.libs.json.{ Json, OFormat }

case class CreateUser(
  name: Option[String],
  email: Option[String],
  avatarURL: Option[String])

object CreateUser {

  implicit val jsonFormat: OFormat[CreateUser] = Json.format[CreateUser]
}