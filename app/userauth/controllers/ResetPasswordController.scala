package userauth.controllers

import java.util.UUID

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ PasswordHasherRegistry, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import common.BaseApplicationController
import controllers.AssetsFinder
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.{ I18nSupport, Messages }
import play.api.mvc.ControllerComponents
import userauth.forms.ResetPasswordForm
import userauth.services.{ AuthTokenService, UserService }
import utils.auth.JwtEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Reset Password` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param authTokenService       The auth token service implementation.
 * @param ex                     The execution context.
 */
class ResetPasswordController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry,
  authTokenService: AuthTokenService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ex: ExecutionContext
) extends BaseApplicationController(components) with I18nSupport {

  /**
   * Resets the password.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def submit(token: UUID) = silhouette.UnsecuredAction.async { implicit request =>
    authTokenService.validate(token).flatMap { maybeToken =>
      logger.info(s"Token returned: $maybeToken")
      maybeToken match {
        case Some(authToken) =>
          ResetPasswordForm.form.bindFromRequest.fold(
            form => Future.successful(badRequest(form.errors)),
            password => userService.retrieve(authToken.userID).flatMap { maybeUser =>
              logger.info(s"Maybe user returned: $maybeUser")
              maybeUser match {
                case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
                  val passwordInfo = passwordHasherRegistry.current.hash(password)
                  authInfoRepository.update[PasswordInfo](user.loginInfo, passwordInfo).map { _ =>
                    Ok
                  }
                case _ => Future.successful(badRequest(Messages("invalid.reset.link")))
              }
            }
          )
        case None => Future.successful(badRequest(Messages("invalid.reset.link")))
      }
    }
  }

}
