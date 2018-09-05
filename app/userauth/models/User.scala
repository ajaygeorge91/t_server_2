package userauth.models

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.libs.json.{ Json, OFormat }

/**
 * The user object.
 *
 * @param id The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param name Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
  id: Long,
  loginInfo: LoginInfo,
  name: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  activated: Boolean) extends Identity {

}

object User {

  implicit val jsonFormat: OFormat[User] = Json.format[User]
}