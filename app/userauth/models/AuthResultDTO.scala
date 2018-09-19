package models

import play.api.libs.json.{ Json, OFormat }
import userauth.models.User

/**
 * Created by ajay on 3/19/2016.
 */

case class AuthResultDTO(
  token: String,
  user: Option[User] = None
)

object AuthResultDTO {

  implicit val jsonFormat: OFormat[AuthResultDTO] = Json.format[AuthResultDTO]
}