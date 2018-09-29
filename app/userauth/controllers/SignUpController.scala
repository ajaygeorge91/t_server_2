package userauth.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{ PasswordHasher, PasswordHasherRegistry }
import com.mohiva.play.silhouette.impl.providers._
import common.BaseApplicationController
import controllers.AssetsFinder
import javax.inject.Inject
import models.AuthResultDTO
import org.webjars.play.WebJarsUtil
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AnyContent, ControllerComponents, Request }
import userauth.models.{ CreateUser, SignUp }
import userauth.services.{ AuthTokenService, UserService }
import utils.auth.JwtEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Sign Up` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param authTokenService       The auth token service implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 * @param ex                     The execution context.
 */
class SignUpController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient
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
  def submit = silhouette.UnsecuredAction.async(parse.json[SignUp]) { implicit request =>
    val data = request.body
    val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        Future.successful(failure(Messages("email.already.exist")))
      case None =>
        val authInfo = passwordHasherRegistry.current.hash(data.password)
        val user = CreateUser(
          name = Some(data.name),
          email = Some(data.email),
          avatarURL = None
        )
        for {
          avatar <- avatarService.retrieveURL(data.email)
          user <- userService.create(user.copy(avatarURL = avatar), loginInfo)
          _ <- authInfoRepository.add(loginInfo, authInfo)
          authenticator <- silhouette.env.authenticatorService.create(loginInfo)
          token <- silhouette.env.authenticatorService.init(authenticator)
        } yield {
          silhouette.env.eventBus.publish(SignUpEvent(user, request))
          silhouette.env.eventBus.publish(LoginEvent(user, request))
          success(AuthResultDTO(token, Some(user)))
        }
    }
  }

}
