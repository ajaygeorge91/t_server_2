package controllers

import com.mohiva.play.silhouette.api.Silhouette
import common.BaseApplicationController
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import utils.auth.JwtEnv

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class ApplicationController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnv]
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder
) extends BaseApplicationController(components) with I18nSupport {

  def index = Action.async { implicit request =>
    Future.successful(Ok(views.html.dashboard(None)))
  }

  def user = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson(request.identity)))
  }

  def map1 = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.draggable_1(request.identity)))
  }

  def map2 = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.display_1(request.identity)))
  }

}
