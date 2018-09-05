package userauth.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth2.FacebookProvider
import common.BaseApplicationController
import javax.inject.Inject
import models.AuthResultDTO
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.json.JsValue
import play.api.mvc._
import userauth.services.UserService
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The social auth controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info service implementation.
 * @param socialProviderRegistry The social provider registry.
 * @param ex                     The execution context.
 */
class SocialAuthController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  socialProviderRegistry: SocialProviderRegistry
)(
  implicit
  ex: ExecutionContext
) extends BaseApplicationController(components) with I18nSupport {

  /**
   * Authenticates a user against a social provider.
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def authenticate(provider: String): Action[AnyContent] = Action.async { implicit request =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
            token <- silhouette.env.authenticatorService.init(authenticator)
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            val result = AuthResultDTO(token, Some(user))
            success(result)
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        failure(Messages("could.not.authenticate"))
    }
  }

  def authenticateFB(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      request.body.asOpt[OAuth2Info] match {
        case Some(authInfo) =>
          (socialProviderRegistry.get[FacebookProvider]("facebook") match {
            case Some(p: FacebookProvider) =>
              for {
                profile <- p.retrieveProfile(authInfo)
                user <- userService.save(profile)
                authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
                authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
                token <- silhouette.env.authenticatorService.init(authenticator)
              } yield {
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                val result = AuthResultDTO(token, Some(user))
                success(result)
              }
            case _ => Future.failed(new ProviderException(s"Cannot authenticate with facebook"))
          }).recover {
            case e: ProviderException =>
              logger.error("Unexpected provider error", e)
              failure(Messages("could.not.authenticate"))
          }
        case _ =>
          Future.successful(failure("Bad OAuth2 json."))
      }
  }

}
