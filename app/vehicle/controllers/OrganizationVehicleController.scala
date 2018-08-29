package vehicle.controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.typesafe.scalalogging.LazyLogging
import controllers.AssetsFinder
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import userauth.forms.NameForm
import utils.auth.DefaultEnv
import vehicle.VehicleService
import vehicle.models.CreateVehicle

import scala.concurrent.{ExecutionContext, Future}

class OrganizationVehicleController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  vehicleService: VehicleService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  executionContext: ExecutionContext,
) extends AbstractController(components) with I18nSupport with LazyLogging {

  def list(organizationId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    vehicleService.list(request.identity.id,organizationId).map{ list =>
      Ok(views.html.vehicle_list(organizationId, request.identity,list))
    }
  }

  def find(organizationId: Long, vehicleId:Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    vehicleService.find(request.identity.id,organizationId,vehicleId).map {
      case Some(vehicle) => Ok(views.html.vehicle_single(request.identity, vehicle))
      case None => NotFound
    }
  }

  def createPage(organizationId: Long): Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    Ok(views.html.vehicle_create(organizationId, NameForm.form))
  }

  def create(organizationId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    NameForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.vehicle_create(organizationId, form))),
      data => {
        val vehicle = CreateVehicle(
          name = data.name,
          image = None,
          description = None,
          address = None,
          geo = None,
          email = None,
          telephone = None,
          publicAccess = true
        )
        vehicleService.create(request.identity.id,organizationId, vehicle).map { vehicle =>
          Ok(views.html.vehicle_single(request.identity, vehicle))
        }
      }
    )
  }

}
