package organization.controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.typesafe.scalalogging.LazyLogging
import common.BaseApplicationController
import controllers.AssetsFinder
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import organization.OrganizationService
import organization.models.CreateOrganization
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class OrganizationController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  organizationService: OrganizationService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  executionContext: ExecutionContext,
) extends BaseApplicationController(components) with I18nSupport with LazyLogging {

  def list: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    organizationService.list(request.identity.id).map{ list =>
      success(list)
    } recover {
      case NonFatal(e)               => failure(e)
    }
  }

  def find(organizationId:Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    organizationService.find(organizationId).map {
      case Some(vehicle) =>success(vehicle)
      case None => notFound(s"Can't find organizationId with Id : $organizationId")
    } recover {
      case NonFatal(e)               => failure(e)
    }
  }

  def create: Action[CreateOrganization] = silhouette.SecuredAction.async(parse.json[CreateOrganization]) { implicit request =>
    organizationService.create(request.identity.id, request.body).map { org =>
      success(org)
    } recover {
      case NonFatal(e) => failure(e)
    }
  }


}
