package userauth.controllers

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import common.BaseApplicationController
import controllers.AssetsFinder
import javax.inject.Inject
import models.AuthResultDTO
import net.ceedubs.ficus.Ficus._
import org.webjars.play.WebJarsUtil
import play.api.Configuration
import play.api.i18n.{ I18nSupport, Messages }
import play.api.mvc.{ Action, ControllerComponents }
import userauth.models.SignIn
import userauth.services.UserService
import utils.auth.DefaultEnv

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Sign In` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param credentialsProvider    The credentials provider.
 * @param socialProviderRegistry The social provider registry.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 */
class SignInController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ex: ExecutionContext
) extends BaseApplicationController(components) with I18nSupport {

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit: Action[SignIn] = silhouette.UnsecuredAction.async(parse.json[SignIn]) { implicit request =>
    val data = request.body
    val credentials = Credentials(data.email, data.password)
    credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          silhouette.env.authenticatorService.create(loginInfo).map {
            authenticator => authenticator
          }.flatMap { authenticator =>
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            silhouette.env.authenticatorService.init(authenticator).map { token =>
              val result = AuthResultDTO(token, Some(user))
              success(result)
            }
          }
        case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
      }
    }.recover {
      case e: ProviderException =>
        logger.error(e.getMessage, e)
        failure(Messages("invalid.credentials"))
    }
  }

}
