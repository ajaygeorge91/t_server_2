package userauth.controllers

import java.net.URLDecoder
import java.util.UUID

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import common.BaseApplicationController
import javax.inject.Inject
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AnyContent, ControllerComponents, Request }
import userauth.services.{ AuthTokenService, UserService }
import utils.auth.JwtEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Activate Account` controller.
 *
 * @param components       The Play controller components.
 * @param silhouette       The Silhouette stack.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param ex               The execution context.
 */
class ActivateAccountController @Inject() (
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
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  def send(email: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = success(Messages("activation.email.sent", decodedEmail))

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.id).map { authToken =>
          val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()

          mailerClient.send(Email(
            subject = Messages("email.activate.account.subject"),
            from = Messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.emails.activateAccount(user, url).body),
            bodyHtml = Some(views.html.emails.activateAccount(user, url).body)
          ))
          result
        }
      case None => Future.successful(result)
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.update(user.copy(activated = true)).map { _ =>
            success(Messages("account.activated"))
          }
        case _ => Future.successful(apiFailureResponse(CONFLICT, Map("message" -> Messages("invalid.activation.link"))))
      }
      case None => Future.successful(apiFailureResponse(CONFLICT, Map("message" -> Messages("invalid.activation.link"))))
    }
  }
}

