package userauth.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import common.BaseApplicationController
import javax.inject.Inject
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.json.Json
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.ControllerComponents
import userauth.forms.ForgotPasswordForm
import userauth.services.{ AuthTokenService, UserService }
import utils.auth.JwtEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Forgot Password` controller.
 *
 * @param components       The Play controller components.
 * @param silhouette       The Silhouette stack.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param ex               The execution context.
 */
class ForgotPasswordController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnv],
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient
)(
  implicit
  ex: ExecutionContext
) extends BaseApplicationController(components) with I18nSupport {

  /**
   * Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @return The result to display.
   */

  def submit = silhouette.UnsecuredAction.async { implicit request =>
    ForgotPasswordForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(Json.obj("errors" -> form.errors.map { _.messages.mkString(", ") }))),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            authTokenService.create(user.id).map { authToken =>
              val url = "http:// TODO "
              // TODO

              mailerClient.send(Email(
                subject = Messages("email.reset.password.subject"),
                from = Messages("email.from"),
                to = Seq(email),
                bodyText = Some(views.txt.emails.resetPassword(user, url).body),
                bodyHtml = Some(views.html.emails.resetPassword(user, url).body)
              ))
              success(Messages("reset.email.sent"))
            }
          case _ => Future.successful(Ok)
        }
      }
    )
  }

}
