package organization.controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.typesafe.scalalogging.LazyLogging
import controllers.AssetsFinder
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import organization.OrganizationService
import organization.models.CreateOrganization
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import userauth.forms.NameForm
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class OrganizationController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  organizationService: OrganizationService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  executionContext: ExecutionContext,
) extends AbstractController(components) with I18nSupport with LazyLogging {

  def list: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    organizationService.list(request.identity.id).map{ list =>
      Ok(views.html.organization_list(request.identity,list))
    }
  }

  def find(organizationId:Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    organizationService.find(organizationId).map {
      case Some(organization) => Ok(views.html.organization_single(request.identity, organization))
      case None => NotFound
    }
  }

  def createPage: Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    Ok(views.html.organization_create(NameForm.form))
  }

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    NameForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.organization_create(form))),
      data => {
        val org = CreateOrganization(
          name = data.name,
          image = None,
          description = None,
          address = None,
          geo = None,
          email = None,
          telephone = None,
          publicAccess = true
        )
        organizationService.create(request.identity.id, org).map { org =>
          Ok(views.html.organization_single(request.identity, org))
        }
      }
    )
  }

}
