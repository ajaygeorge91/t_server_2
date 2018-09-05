package vehicle.controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.typesafe.scalalogging.LazyLogging
import common.BaseApplicationController
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import utils.auth.DefaultEnv
import vehicle.VehicleService
import vehicle.models.CreateVehicle

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class OrganizationVehicleController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  vehicleService: VehicleService
)(implicit ex: ExecutionContext)
  extends BaseApplicationController(components) with I18nSupport with LazyLogging {

  def list(organizationId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    vehicleService.list(request.identity.id, organizationId).map { list =>
      success(list)
    } recover {
      case NonFatal(e) => failure(e)
    }
  }

  def find(organizationId: Long, vehicleId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    vehicleService.find(request.identity.id, organizationId, vehicleId).map {
      case Some(vehicle) => success(vehicle)
      case None => notFound(s"Can't find vehicle with Id : $vehicleId")
    } recover {
      case NonFatal(e) => failure(e)
    }
  }

  def create(organizationId: Long): Action[CreateVehicle] = silhouette.SecuredAction.async(parse.json[CreateVehicle]) { implicit request =>
    vehicleService.create(request.identity.id, organizationId, request.body).map { vehicle =>
      success(vehicle)
    } recover {
      case NonFatal(e) => failure(e)
    }
  }

}
