package userauth.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordHasherRegistry, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import common.BaseApplicationController
import javax.inject.Inject
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import userauth.forms.ChangePasswordForm
import utils.auth.{ JwtEnv, WithProvider }

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Change Password` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param credentialsProvider    The credentials provider.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param ex                     The execution context.
 */
class ChangePasswordController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnv],
  credentialsProvider: CredentialsProvider,
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry
)(
  implicit
  ex: ExecutionContext
) extends BaseApplicationController(components) with I18nSupport {

  /**
   * Changes the password.
   *
   * @return The result to display.
   */
  def submit = silhouette.SecuredAction(WithProvider[JwtEnv#A](CredentialsProvider.ID)).async { implicit request =>
    ChangePasswordForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(Json.obj("errors" -> form.errors.map { _.messages.mkString(", ") }))),
      password => {
        val (currentPassword, newPassword) = password
        val credentials = Credentials(request.identity.email.getOrElse(""), currentPassword)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
          authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { u =>
            success(Messages("password.changed"))
          }
        }.recover {
          case e: ProviderException =>
            failure(Messages("current.password.invalid"))
        }
      }
    )
  }

}
