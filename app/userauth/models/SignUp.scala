package userauth.models

import play.api.libs.json.{ Json, OFormat }

case class SignUp(
  name: String,
  email: String,
  password: String)

object SignUp {

  implicit val jsonFormat: OFormat[SignUp] = Json.format[SignUp]
}