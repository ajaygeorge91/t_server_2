package userauth.forms

import play.api.data.Forms._
import play.api.data._

/**
 * The `Change Password` form.
 */
object ChangePasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(tuple(
    "currentPassword" -> nonEmptyText,
    "newPassword" -> nonEmptyText
  ))

  case class Data(
    currentPassword: String,
    newPassword: String)
}
