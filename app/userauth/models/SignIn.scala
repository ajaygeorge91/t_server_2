package userauth.models

import play.api.libs.json.{ Json, OFormat }

case class SignIn(
  email: String,
  password: String)

object SignIn {

  implicit val jsonFormat: OFormat[SignIn] = Json.format[SignIn]
}