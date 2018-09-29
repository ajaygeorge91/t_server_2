package userauth.controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import common.BaseApplicationController
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import utils.auth.JwtEnv

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 */
class SignOutController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnv]
) extends BaseApplicationController(components) with I18nSupport {

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut: Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[JwtEnv, AnyContent] =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, success("Logged out"))
  }

}
